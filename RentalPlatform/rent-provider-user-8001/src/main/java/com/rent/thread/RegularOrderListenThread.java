package com.rent.thread;

import com.rent.dao.OrderPayMapper;
import com.rent.dao.TradeMapper;
import com.rent.pojo.base.Trade;
import com.rent.util.MyUtil;
import lombok.SneakyThrows;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author w
 */
public class RegularOrderListenThread implements Runnable{
    private String dueDate;
    private String orderId;
    private TradeMapper tradeMapper;

    public RegularOrderListenThread(String dueDate, String orderId, TradeMapper tradeMapper) {
        this.dueDate = dueDate;
        this.orderId = orderId;
        this.tradeMapper = tradeMapper;
    }

    @SneakyThrows
    @Override
    public void run() {
        Logger logger = LoggerFactory.getLogger(RegularOrderListenThread.class);
        logger.info("定期订单："+orderId+" 开启日期监听");
        //提醒的小时数
        int time=60;
        while (true){
            //六小时判断一次
            Thread.sleep(1000 * 60 * time);
            String nowTime = MyUtil.getNowTime();
            int i = MyUtil.nDaysBetweenTwoDate(nowTime, dueDate);
            if (i<=1&&i>=0){
                Trade trade = tradeMapper.mySelectById(orderId);
                //如果在到期前一天内，并且订单还未结束则提醒
                if (trade.getOrderState()==4){
                    logger.info("提醒定期订单！"+orderId);
                    //快到期了就一个小时提醒一次
                    time=1;
                //否则结束线程
                }else {
                    logger.info("定期订单："+orderId+" 监听结束");
                    break;
                }
            }else if (i<0){
                //过期订单处理
                logger.info("定期订单："+orderId+" 已过期，执行过期处理");
                break;
            }
            logger.info("定期订单："+orderId+" 正常使用");
        }
    }
}
