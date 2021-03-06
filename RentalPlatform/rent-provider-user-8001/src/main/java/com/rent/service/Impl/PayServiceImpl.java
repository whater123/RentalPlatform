package com.rent.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.config.ThreadPoolConfig;
import com.rent.dao.*;
import com.rent.pojo.base.Trade;
import com.rent.pojo.base.OrderPay;
import com.rent.pojo.base.manager.Enterprise;
import com.rent.pojo.base.manager.EnterpriseGoods;
import com.rent.pojo.base.manager.EnterpriseGoodsEntity;
import com.rent.pojo.base.user.User;
import com.rent.pojo.view.PayNeedMsg;
import com.rent.service.GoodsService;
import com.rent.service.LoginAndRegisterService;
import com.rent.service.PayService;
import com.rent.thread.RegularOrderListenThread;
import com.rent.thread.SMSThread;
import com.rent.util.MoneyUtil;
import com.rent.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author w
 */
@Service
public class PayServiceImpl implements PayService {
    @Autowired
    LoginAndRegisterService loginAndRegisterService;
    @Autowired
    GoodsService goodsService;
    @Autowired
    TradeMapper tradeMapper;
    @Autowired
    GoodsEntityMapper goodsEntityMapper;
    @Autowired
    OrderPayMapper orderPayMapper;
    @Autowired
    UserMapper userMapper;
    @Autowired
    EnterpriseMapper enterpriseMapper;
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    ThreadPoolConfig threadPoolConfig;

    /**
     * 获取支付id
     * @param userId 用户id
     * @return 半随机支付id
     */
    private String getRandomPayId(int userId){
        StringBuilder stringBuilder = new StringBuilder();
        Integer integer = orderPayMapper.selectCount(null);
        stringBuilder.append("RPP");
        stringBuilder.append(integer+1);
        stringBuilder.append(UUID.randomUUID().toString().toUpperCase(), 0, 5);
        stringBuilder.append(userId);
        return String.valueOf(stringBuilder);
    }

    @Override
    public PayNeedMsg getPayNeedMsg(Trade trade) throws Exception {
        if (trade.getOrderRentWay()!=1 && trade.getOrderRentWay()!=2){
            return new PayNeedMsg("rantWay参数错误");
        }
        User user = loginAndRegisterService.getUser(new User(trade.getUserId()));
        EnterpriseGoodsEntity goodsEntityInformation = goodsService.getGoodsEntityInformation(trade.getGoodsEntityId());
        if (user==null||goodsEntityInformation==null){
            return new PayNeedMsg("id参数错误");
        }
        String subDeposit = MoneyUtil.fractionMultiply(MoneyUtil.fractionDivide(user.getUserCreditScore() +".0", "240.0"), goodsEntityInformation.getGoodsDeposit());
        String needDeposit = MoneyUtil.fractionSubtract(goodsEntityInformation.getGoodsDeposit(),subDeposit);
        subDeposit = MoneyUtil.fractionMultiply(subDeposit, trade.getOrderGoodsCount()+".0");
        needDeposit = MoneyUtil.fractionMultiply(needDeposit, trade.getOrderGoodsCount()+".0");
        if (needDeposit.contains("-")){
            needDeposit = "0";
            subDeposit = goodsEntityInformation.getGoodsDeposit();
        }
        if (trade.getOrderRentWay()==2){
            //活期支付总额就是押金金额
            return new PayNeedMsg(user.getUserCreditScore(),needDeposit,needDeposit,subDeposit);
        } else if (trade.getOrderRentWay()==1){
            //定期时间单位对应价格获取，如果不存在就返回null
            String[] split = goodsEntityInformation.getGoodsRegularUnit().split("/");
            String[] split1 = goodsEntityInformation.getGoodsRegularPrice().split("/");
            if (split.length!=split1.length){
                return new PayNeedMsg("数据库存储错误");
            }
            boolean flag = false;
            String rentPrice = null;
            for (int i = 0; i < split.length; i++) {
                if (split[i].equals(trade.getOrderRentUnit())){
                    rentPrice = split1[i];
                    flag = true;
                    break;
                }
            }
            if (!flag||rentPrice==null){
                return new PayNeedMsg("orderRentUnit参数错误，不存在此单位");
            }
            //单位价格*几个单位
            String firstPay = MoneyUtil.fractionMultiply(MoneyUtil.addTail(rentPrice), trade.getOrderRentTime() +".0");
            firstPay = MoneyUtil.fractionMultiply(firstPay, trade.getOrderGoodsCount()+".0");
            String goodsOriginPrice = MoneyUtil.fractionMultiply(goodsEntityInformation.getGoodsPrice(), trade.getOrderGoodsCount() +".0");
            //如果第一次支付金额大于原价则返回参数错误
            if (MoneyUtil.compare(firstPay,goodsOriginPrice)==1){
                return new PayNeedMsg("定期支付金额大于商品原价,定期支付金额:"+firstPay+";商品原价"+goodsEntityInformation.getGoodsPrice());
            }
            //需要支付总价=押金+定期价格
            String firstPayAdd = MoneyUtil.fractionAdd(needDeposit, firstPay);
            return new PayNeedMsg(user.getUserCreditScore(),needDeposit,firstPayAdd,subDeposit);
        }else {
            return null;
        }
    }

    @Override
    public Map<String, String> payMsgCheck(OrderPay orderPay) throws Exception {
        Map<String,String> map = new HashMap<>();
        EnterpriseGoodsEntity enterpriseGoodsEntity = goodsEntityMapper.selectById(orderPay.getGoodsEntityId());
        if (enterpriseGoodsEntity==null || enterpriseGoodsEntity.getGoodsRentState()<2){
            map.put("code","1");
            map.put("msg","商品个体不存在或不为租赁状态");
            return map;
        }
        Trade trade = tradeMapper.mySelectById(orderPay.getOrderId());
        QueryWrapper<OrderPay> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderPay.getOrderId());
        List<OrderPay> orderPays = orderPayMapper.selectList(queryWrapper);
        if (orderPays.size()>=1){
            for (OrderPay o: orderPays
                 ) {
                if (o.getPayType()==0&&orderPay.getPayType()==0){
                    map.put("code", "309");
                    map.put("msg", "订单已初付款");
                    return map;
                }
            }
        }
        if (trade ==null) {
            map.put("code", "309");
            map.put("msg", "订单不存在");
            return map;
        }else if (trade.getUserId()!=orderPay.getUserId()){
            map.put("code", "309");
            map.put("msg", "订单提交者付款者不一致");
            return map;
        }
        if (orderPay.getOrderNeedPay()==null){
            orderPay.setPayAmount(trade.getOrderTotalMoney());
        }else {
            orderPay.setPayAmount(orderPay.getOrderNeedPay());
        }
        User user = loginAndRegisterService.getUser(new User(trade.getUserId()));
        if (!MoneyUtil.isRuleString(orderPay.getPayAmount())){
            orderPay.setPayAmount(MoneyUtil.addTail(orderPay.getPayAmount()));
        }
        if (!MoneyUtil.isRuleString(user.getUserAccountMoney())){
            user.setUserAccountMoney(MoneyUtil.addTail(user.getUserAccountMoney()));
        }
        if (orderPay.getPayType()>3){
            map.put("code","1");
            map.put("msg","payType参数错误");
            return map;
        }else if (MoneyUtil.compare(orderPay.getPayAmount(),user.getUserAccountMoney())==1){
            map.put("code","308");
            map.put("msg","用户余额不足");
            return map;
        } else {
            map.put("code","0");
            return map;
        }
    }

    @Override
    public boolean insertPayAndUpdate(OrderPay orderPay) throws Exception {
        Trade trade = tradeMapper.mySelectById(orderPay.getOrderId());
        if (orderPay.getOrderNeedPay()==null){
            orderPay.setOrderNeedPay(trade.getOrderTotalMoney());
        }

        System.out.println(orderPay);
        Enterprise enterprise = enterpriseMapper.selectById(trade.getEntpId());
        double addScore = Double.parseDouble(MoneyUtil.fractionMultiply(orderPay.getOrderNeedPay(),"0.09"));

        String randomPayId = getRandomPayId(orderPay.getUserId());
        orderPay.setPayId(randomPayId);
        orderPay.setPayTime(MyUtil.getNowTime());
        orderPay.setPayScore(addScore);
        orderPay.setPayAmount(orderPay.getOrderNeedPay());
        if (!"0".equals(trade.getOrderParentId())){
            orderPay.setOrderId(trade.getOrderParentId());
        }

        int insert = orderPayMapper.insert(orderPay);
        if (insert!=1){
            return false;
        }
        if (orderPay.getPayType()==0){
            trade.setOrderState(2);
        }else if (orderPay.getPayType()==3){
            trade.setOrderState(5);
        }else if (orderPay.getPayType()==2){
            trade.setOrderState(6);
        }else if (orderPay.getPayType()==1&&trade.getOrderState()!=4){
            trade.setOrderState(4);
        }
        tradeMapper.updateById(trade);

        User user = userMapper.selectById(orderPay.getUserId());
        String newAccount = MoneyUtil.fractionSubtract(user.getUserAccountMoney(), orderPay.getOrderNeedPay());
        if (newAccount.contains("-")){
            trade.setOrderState(1);
            tradeMapper.updateById(trade);
            orderPayMapper.deleteById(orderPay.getPayId());
            return false;
        }
        user.setUserAccountMoney(newAccount);
        user.setUserCreditScore(user.getUserCreditScore()+(int)addScore);
        enterprise.setEntpAccountMoney(MoneyUtil.fractionAdd(enterprise.getEntpAccountMoney(),orderPay.getOrderNeedPay()));
        int i2 = enterpriseMapper.updateById(enterprise);
        if (i2!=1){
            if (trade.getOrderRentWay()!=2){
                trade.setOrderState(1);
                tradeMapper.updateById(trade);
                orderPayMapper.deleteById(orderPay.getPayId());
                return false;
            }
        }
        int i = userMapper.updateById(user);
        if (i!=1){
            trade.setOrderState(1);
            tradeMapper.updateById(trade);
            enterprise.setEntpAccountMoney(MoneyUtil.fractionDivide(enterprise.getEntpAccountMoney(),orderPay.getOrderNeedPay()));
            enterpriseMapper.updateById(enterprise);
            orderPayMapper.deleteById(orderPay.getPayId());
            return false;
        }else {
            if (trade.getOrderRentWay()==1){
                String orderCreateTime = trade.getOrderCreateTime();
                String orderRentUnit = trade.getOrderRentUnit();
                int orderRentTime = trade.getOrderRentTime();
                String s = MyUtil.addDate(orderCreateTime, orderRentTime * Integer.parseInt(orderRentUnit));
                //启动监听线程
                threadPoolConfig.threadPoolTaskExecutor().execute(new RegularOrderListenThread(s,trade.getOrderId(),tradeMapper));
                System.out.println("监听线程已启动");
            }
            return true;
        }
    }

    @Override
    public List<OrderPay> getUserAllPays(int userId) {
        QueryWrapper<OrderPay> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        List<OrderPay> orderPays = orderPayMapper.selectList(queryWrapper);
        Collections.reverse(orderPays);
        orderPays.forEach(orderPay -> {
            EnterpriseGoodsEntity enterpriseGoodsEntity = goodsEntityMapper.selectById(orderPay.getGoodsEntityId());
            if (enterpriseGoodsEntity==null){
                orderPay.setGoodsTitle(null);
            }else {
                EnterpriseGoods enterpriseGoods = goodsMapper.selectById(enterpriseGoodsEntity.getGoodsId());
                if (enterpriseGoods==null){
                    orderPay.setGoodsTitle(null);
                }else {
                    orderPay.setGoodsTitle(enterpriseGoods.getGoodsTitle());
                }
            }
        });
        return orderPays;
    }

    @Override
    public boolean buyGoods(String orderId) throws Exception {
        Trade trade = tradeMapper.mySelectById(orderId);
        if (!"0".equals(trade.getOrderParentId())){
            trade = tradeMapper.mySelectById(trade.getOrderParentId());
        }
        //退款金额
        String orderDeposit = trade.getOrderDeposit();
        Enterprise enterprise = enterpriseMapper.selectById(trade.getEntpId());
        String entpAfterSub = MoneyUtil.fractionSubtract(enterprise.getEntpAccountMoney(), orderDeposit);
        User user = userMapper.selectById(trade.getUserId());
        String userAfterAdd = MoneyUtil.fractionAdd(user.getUserAccountMoney(), orderDeposit);
        EnterpriseGoodsEntity enterpriseGoodsEntity = goodsEntityMapper.selectById(trade.getGoodsEntityId());
        enterpriseGoodsEntity.setGoodsRentState(5);

        QueryWrapper<OrderPay> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_id",orderId);
        List<OrderPay> orderPays = orderPayMapper.selectList(queryWrapper);
        OrderPay orderPay = orderPays.get(0);
        orderPay.setPayId(getRandomPayId(orderPay.getUserId()));
        orderPay.setPayTime(MyUtil.getNowTime());
        orderPay.setPayScore(0);
        orderPay.setPayPlatform("租赁宝");
        orderPay.setPayType(4);
        orderPay.setPayAmount("-"+orderDeposit);

        int insert = orderPayMapper.insert(orderPay);
        if (insert!=1){
            return false;
        }
        int i = goodsEntityMapper.updateById(enterpriseGoodsEntity);
        if (i!=1){
            orderPayMapper.deleteById(orderPay.getPayId());
            return false;
        }
        enterprise.setEntpAccountMoney(entpAfterSub);
        int i1 = enterpriseMapper.updateById(enterprise);
        if (i1!=1){
            orderPayMapper.deleteById(orderPay.getPayId());
            enterpriseGoodsEntity.setGoodsRentState(4);
            goodsEntityMapper.updateById(enterpriseGoodsEntity);
            return false;
        }
        user.setUserAccountMoney(userAfterAdd);
        int i2 = userMapper.updateById(user);
        if (i2!=1){
            orderPayMapper.deleteById(orderPay.getPayId());
            enterpriseGoodsEntity.setGoodsRentState(4);
            goodsEntityMapper.updateById(enterpriseGoodsEntity);
            enterprise.setEntpAccountMoney(MoneyUtil.fractionAdd(entpAfterSub,orderDeposit));
            enterpriseMapper.updateById(enterprise);
            return false;
        }
        return true;
    }
}
