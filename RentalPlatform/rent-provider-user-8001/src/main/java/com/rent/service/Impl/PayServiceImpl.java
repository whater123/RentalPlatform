package com.rent.service.Impl;

import com.rent.pojo.base.Order;
import com.rent.pojo.base.manager.EnterpriseGoodsEntity;
import com.rent.pojo.base.user.User;
import com.rent.pojo.view.PayNeedMsg;
import com.rent.service.GoodsService;
import com.rent.service.LoginAndRegisterService;
import com.rent.service.PayService;
import com.rent.util.MoneyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author w
 */
@Service
public class PayServiceImpl implements PayService {
    @Autowired
    LoginAndRegisterService loginAndRegisterService;
    @Autowired
    GoodsService goodsService;

    @Override
    public PayNeedMsg getPayNeedMsg(Order order) throws Exception {
        if (order.getOrderRentWay()!=1 && order.getOrderRentWay()!=2){
            return new PayNeedMsg("rantWay参数错误");
        }
        User user = loginAndRegisterService.getUser(new User(order.getUserId()));
        EnterpriseGoodsEntity goodsEntityInformation = goodsService.getGoodsEntityInformation(order.getGoodsEntityId());
        if (user==null||goodsEntityInformation==null){
            return new PayNeedMsg("id参数错误");
        }
        String subDeposit = MoneyUtil.fractionMultiply(MoneyUtil.fractionDivide(user.getUserCreditScore() +".0", "240.0"), goodsEntityInformation.getGoodsDeposit());
        String needDeposit = MoneyUtil.fractionSubtract(goodsEntityInformation.getGoodsDeposit(),subDeposit);
        if (order.getOrderRentWay()==2){
            //活期支付总额就是押金金额
            return new PayNeedMsg(user.getUserCreditScore(),needDeposit,needDeposit,subDeposit);
        } else if (order.getOrderRentWay()==1){
            //定期时间单位对应价格获取，如果不存在就返回null
            String[] split = goodsEntityInformation.getGoodRegularUnit().split("/");
            String[] split1 = goodsEntityInformation.getGoodsRegularPrice().split("/");
            if (split.length!=split1.length){
                return new PayNeedMsg("数据库存储错误");
            }
            boolean flag = false;
            String rentPrice = null;
            for (int i = 0; i < split.length; i++) {
                if (split[i].equals(order.getOrderRentUnit())){
                    rentPrice = split1[i];
                    flag = true;
                    break;
                }
            }
            if (!flag||rentPrice==null){
                return new PayNeedMsg("orderRentUnit参数错误，不存在此单位");
            }
            //单位价格*几个单位
            String firstPay = MoneyUtil.fractionMultiply(rentPrice+".0", order.getOrderRentTime() +".0");
            //如果第一次支付金额大于原价则返回参数错误
            if (MoneyUtil.compare(firstPay,goodsEntityInformation.getGoodsPrice())==1){
                return new PayNeedMsg("定期支付金额大于商品原价,定期支付金额:"+firstPay+";商品原价"+goodsEntityInformation.getGoodsPrice());
            }
            //需要支付总价=押金+定期价格
            String firstPayAdd = MoneyUtil.fractionAdd(needDeposit, firstPay);
            return new PayNeedMsg(user.getUserCreditScore(),needDeposit,firstPayAdd,subDeposit);
        }else {
            return null;
        }
    }
}
