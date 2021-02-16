package com.rent.service.Impl;

import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.*;
import com.rent.pojo.base.OrderLogistics;
import com.rent.pojo.base.Trade;
import com.rent.pojo.base.manager.EnterpriseGoods;
import com.rent.pojo.base.manager.EnterpriseGoodsEntity;
import com.rent.pojo.base.user.Contact;
import com.rent.pojo.view.LogisticsMsg;
import com.rent.pojo.view.ResBody;
import com.rent.service.OrderService;
import com.rent.util.HttpUtils;
import com.rent.util.MyUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * @author w
 */
@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    TradeMapper tradeMapper;
    @Autowired
    GoodsEntityMapper goodsEntityMapper;
    @Autowired
    GoodsMapper goodsMapper;
    @Autowired
    ContactMapper contactMapper;
    @Autowired
    LogisticsMapper logisticsMapper;

    /**
     * 获取订单id
     * @param userId 用户id
     * @return 半随机订单id
     */
    private String getRandomOrderId(int userId){
        StringBuilder stringBuilder = new StringBuilder();
        Integer integer = tradeMapper.selectCount(null);
        stringBuilder.append("RPO");
        stringBuilder.append(integer+1);
        stringBuilder.append(UUID.randomUUID().toString().toUpperCase(), 0, 5);
        stringBuilder.append(userId);
        return String.valueOf(stringBuilder);
    }

    @Override
    public Map<String,String> insertOriginOrder(Trade trade) {
        Map<String,String> map = new HashMap<>();
        EnterpriseGoodsEntity enterpriseGoodsEntity = goodsEntityMapper.selectById(trade.getGoodsEntityId());
        if (enterpriseGoodsEntity.getGoodsRentState()!=0&&enterpriseGoodsEntity.getGoodsRentState()!=1){
            map.put("code","1");
            map.put("msg","商品为不可租状态");
            return map;
        }
        QueryWrapper<Contact> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("contact_receive_id","U"+trade.getUserId())
                    .eq("contact_id",trade.getContactId());
        Contact contacts = contactMapper.selectOne(queryWrapper);
        if (contacts==null){
            map.put("code","1");
            map.put("msg","联系方式不存在");
            return map;
        }
        int updateRentState = goodsEntityMapper.updateRentState(trade.getGoodsEntityId(), trade.getOrderRentWay() + 1);
        if (updateRentState!=1){
            map.put("code","1");
            map.put("msg","修改商品租赁状态失败");
            return map;
        }
        //销量增加
        EnterpriseGoods enterpriseGoods = goodsMapper.selectById(enterpriseGoodsEntity.getGoodsId());
        if (enterpriseGoods==null){
            goodsEntityMapper.updateRentState(trade.getGoodsEntityId(), 1);
            map.put("code","1");
            map.put("msg","该商品的商品集不存在");
            return map;
        }
        if (enterpriseGoods.getGoodsExisting()<trade.getOrderGoodsCount()){
            map.put("code","1");
            map.put("msg","商品库存不足");
            return map;
        }
        enterpriseGoods.setGoodsRent(enterpriseGoods.getGoodsRent()+ trade.getOrderGoodsCount());
        enterpriseGoods.setGoodsExisting(enterpriseGoods.getGoodsExisting()-trade.getOrderGoodsCount());
        int i = goodsMapper.updateById(enterpriseGoods);
        if (i!=1){
            goodsEntityMapper.updateRentState(trade.getGoodsEntityId(), 1);
            map.put("code","1");
            map.put("msg","修改商品集销量与库存失败");
            return map;
        }
        trade.setLogisticsId(0);
        trade.setOrderParentId("0");
        trade.setOrderCreateTime(MyUtil.getNowTime());
        trade.setOrderId(getRandomOrderId(trade.getUserId()));
        trade.setEntpId(enterpriseGoods.getEntpId());
        trade.setOrderState(1);
        //这里的逻辑需要改一下，处理错误的回滚
        int insert = tradeMapper.insert(trade);
        if (insert!=1){
            goodsEntityMapper.updateRentState(trade.getGoodsEntityId(), 1);
            enterpriseGoods.setGoodsRent(enterpriseGoods.getGoodsRent()- trade.getOrderGoodsCount());
            goodsMapper.updateById(enterpriseGoods);
            map.put("code","1");
            map.put("msg","订单提交失败");
            return map;
        }
        map.put("code","0");
        return map;
    }

    @Override
    public List<Trade> getAllTrades(int userId) {
        QueryWrapper<Trade> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("user_id",userId);
        return tradeMapper.selectList(queryWrapper);
    }

    @Override
    public List<ResBody> getOrderLogistics(String orderId) throws Exception {
        Trade trade = tradeMapper.mySelectById(orderId);
        if (trade==null){
            throw new Exception("订单不存在");
        }
        OrderLogistics orderLogistics = logisticsMapper.selectById(trade.getLogisticsId());
        Contact contact = contactMapper.selectById(trade.getContactId());
        if (contact==null){
            throw new Exception("联系方式不存在");
        }
        if (orderLogistics==null){
            //说明是未发货
            return null;
        }
        List<ResBody> logisticsMsgs = new ArrayList<>();
        ResBody oneLogisticsMsg = getOneLogisticsMsg(
                orderLogistics.getEntpToUserCompany(), orderLogistics.getEntpToUserNumber(), contact.getContactPhone());
        logisticsMsgs.add(oneLogisticsMsg);
        //判断下面参数是否为空，不为空就继续操作
//        ResBody oneLogisticsMsg1 = getOneLogisticsMsg(
//                orderLogistics.getUserToEntpCompany(), orderLogistics.getUserToEntpNumber(), contact.getContactPhone());

        return logisticsMsgs;
    }

    private ResBody getOneLogisticsMsg(String com,String nu,String receiverPhone){
        String host = "https://ali-deliver.showapi.com";
        String path = "/showapi_expInfo";
        String method = "GET";
        String appcode = "75597b63850c493fa533ef6515cdce5f";
        Map<String, String> headers = new HashMap<String, String>();
        //最后在header中的格式(中间是英文空格)为Authorization:APPCODE 83359fd73fe94948385f570e3c139105
        headers.put("Authorization", "APPCODE " + appcode);
        Map<String, String> querys = new HashMap<String, String>();
        //快递公司字母简称
        querys.put("com", com);
        //单号
        querys.put("nu", nu);
        //收/寄件人手机号后四位，顺丰快递必须填写本字段。
//        querys.put("receiverPhone", contact.getContactPhone().substring(contact.getContactPhone().length()-4));
//        querys.put("senderPhone", "senderPhone");
        querys.put("receiverPhone", receiverPhone);

        try {
            HttpResponse response = HttpUtils.doGet(host, path, method, headers, querys);
            //获取response的body
            HttpEntity entity = response.getEntity();
            System.out.println(EntityUtils.toString(response.getEntity()));

            JSONObject jsonObject = JSONObject.parseObject(EntityUtils.toString(response.getEntity()));
            int showApiResCode = jsonObject.getObject("showapi_res_code", Integer.class);
            if (showApiResCode!=0){
                return null;
            }else {
                return jsonObject.getObject("showapi_res_body", ResBody.class);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
