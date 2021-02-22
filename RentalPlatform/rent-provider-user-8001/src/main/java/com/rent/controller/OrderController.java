package com.rent.controller;

import com.rent.dao.AppealMapper;
import com.rent.pojo.base.Appeal;
import com.rent.pojo.base.OrderLogistics;
import com.rent.pojo.base.Trade;
import com.rent.pojo.base.user.UserComment;
import com.rent.pojo.view.PayNeedMsg;
import com.rent.pojo.view.ResBody;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.CommentService;
import com.rent.service.LoginAndRegisterService;
import com.rent.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;

/**
 * @author w
 */
@RestController
@RequestMapping("/user/order")
public class OrderController {
    @Autowired
    OrderService orderService;
    @Autowired
    LoginAndRegisterService loginAndRegisterService;
    @Autowired
    CommentService commentService;
    @Autowired
    AppealMapper appealMapper;

    @PutMapping("/putOriginOrder")
    public ReturnMsg putOriginOrder(@RequestBody Trade trade){
        try{
            boolean b = loginAndRegisterService.userExtendToken(trade.getUserId());
            if (!b){
                return new ReturnMsg("301",true);
            }

            Map<String, String> map = orderService.insertOriginOrder(trade);
            if (!"0".equals(map.get("code"))){
                return new ReturnMsg("1",true, map.get("msg"));
            }else {
                return new ReturnMsg("0",false,(Object) trade.getOrderId());
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @GetMapping("/getAllOrders/{userId}")
    public ReturnMsg getAllOrders(@PathVariable("userId") String userId){
        try{
            if (!loginAndRegisterService.userExtendToken(Integer.parseInt(userId))){
                return new ReturnMsg("301",true);
            }

            List<Trade> allTrades = orderService.getAllTrades(Integer.parseInt(userId));
            return new ReturnMsg("0",false,allTrades);
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @GetMapping("/logistics/{orderId}")
    public ReturnMsg getLogistics(@PathVariable("orderId") String orderId){
        try {
            List<ResBody> orderLogistics = orderService.getOrderLogistics(orderId);
            if (orderLogistics==null||orderLogistics.size()==0){
                return new ReturnMsg("1",true,"订单未发货");
            }else {
                return new ReturnMsg("0",false,orderLogistics);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @PostMapping("/waitConfirm/confirmReceipt")
    public ReturnMsg confirmReceipt(@RequestBody Trade trade, HttpServletRequest request){
        try{
            String userId = request.getHeader("UserId");
            if ("".equals(userId)){
                return new ReturnMsg("301",true);
            }
            if (!loginAndRegisterService.userExtendToken(Integer.parseInt(userId))){
                return new ReturnMsg("301",true);
            }
            if (orderService.confirmReceipt(trade.getOrderId())) {
                return new ReturnMsg("0",false);
            }else {
                return new ReturnMsg("309",true,"订单不存在或状态不为待收货");
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @PostMapping("/using/renewal")
    public ReturnMsg renewal(@RequestBody Trade trade, HttpServletRequest request) throws Exception {
        try{
            String userId = request.getHeader("UserId");
            if ("".equals(userId)){
                return new ReturnMsg("301",true);
            }
            if (!loginAndRegisterService.userExtendToken(Integer.parseInt(userId))){
                return new ReturnMsg("301",true);
            }
            trade.setUserId(Integer.parseInt(userId));
            //续租定期
            if (trade.getOrderRentWay()==1){
                if (trade.getOrderRentUnit()==null||trade.getOrderRentTime()==0||trade.getOrderTotalMoney()==null){
                    return new ReturnMsg("1",true,"定期续租参数不齐");
                }
                Map<String, String> map = orderService.renewalD(trade);
                if (!"0".equals(map.get("code"))){
                    return new ReturnMsg("1",true,map.get("msg"));
                }else {
                    return new ReturnMsg("0",false,(Object)map.get("orderId"));
                }
            }
            //续租活期
            else {
                Map<String, String> map = orderService.renewalH(trade);
                if (!"0".equals(map.get("code"))){
                    return new ReturnMsg("1",true,map.get("msg"));
                }else {
                    return new ReturnMsg("0",false);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @PostMapping("/waitComment/comment")
    public ReturnMsg comment(@RequestBody UserComment userComment,HttpServletRequest request){
        try{
            String userId = request.getHeader("UserId");
            if ("".equals(userId)){
                return new ReturnMsg("301",true);
            }
            if (!loginAndRegisterService.userExtendToken(Integer.parseInt(userId))){
                return new ReturnMsg("301",true);
            }
            userComment.setUserId(Integer.parseInt(userId));
            if (commentService.insertComment(userComment)) {
                return new ReturnMsg("0",false);
            }else {
                return new ReturnMsg("1",true,"无法继续评论！");
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @GetMapping("/using/getBuyMsg/{orderId}")
    public ReturnMsg getBuyMsg(@PathVariable("orderId") String orderId,HttpServletRequest request) throws Exception {
        try{
            String userId = request.getHeader("UserId");
            if ("".equals(userId)){
                return new ReturnMsg("301",true);
            }
            if (!loginAndRegisterService.userExtendToken(Integer.parseInt(userId))){
                return new ReturnMsg("301",true);
            }
            return new ReturnMsg("0",false,orderService.getBuyMsg(orderId));
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @PostMapping("/using/returnGoods")
    public ReturnMsg returnGoods(HttpServletRequest request, @RequestBody OrderLogistics orderLogistics){
        try{
            String userId = request.getHeader("UserId");
            if ("".equals(userId)){
                return new ReturnMsg("301",true);
            }
            if (!loginAndRegisterService.userExtendToken(Integer.parseInt(userId))){
                return new ReturnMsg("301",true);
            }

            if (orderService.updateUserToEntpLo(orderLogistics)) {
                return new ReturnMsg("0",false);
            }else {
                return new ReturnMsg("1",true,"归还失败");
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @GetMapping("/using/getCurrentPay/{orderId}")
    public ReturnMsg getCurrentPay(HttpServletRequest request,@PathVariable("orderId") String orderId) throws Exception {
        try{
            String userId = request.getHeader("UserId");
            if ("".equals(userId)){
                return new ReturnMsg("301",true);
            }
            if (!loginAndRegisterService.userExtendToken(Integer.parseInt(userId))){
                return new ReturnMsg("301",true);
            }
            PayNeedMsg currentPay = orderService.getCurrentPay(orderId);
            if (currentPay.getErrorCode()!=null){
                return new ReturnMsg("309",true,currentPay.getErrorCode());
            }else {
                return new ReturnMsg("0",false,currentPay);
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }

    @PutMapping("/finished/appeal")
    public ReturnMsg appeal(HttpServletRequest request, @RequestBody Appeal appeal){
        try{
            String userId = request.getHeader("UserId");
            if ("".equals(userId)){
                return new ReturnMsg("301",true);
            }
            if (!loginAndRegisterService.userExtendToken(Integer.parseInt(userId))){
                return new ReturnMsg("301",true);
            }
            int insert = appealMapper.insert(appeal);
            if (insert==1){
                return new ReturnMsg("0",false);
            }else {
                return new ReturnMsg("500",true,"数据库错误");
            }
        }catch (Exception e){
            e.printStackTrace();
            return new ReturnMsg("500",true,e.getMessage());
        }
    }
}
