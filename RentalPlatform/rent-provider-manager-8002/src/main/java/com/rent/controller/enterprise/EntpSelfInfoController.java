package com.rent.controller.enterprise;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.config.ShiroUtil;
import com.rent.dao.ContactMapper;
import com.rent.pojo.base.user.Contact;
import com.rent.pojo.base.manager.Enterprise;
import com.rent.pojo.base.manager.EnterpriseAuthentication;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.EnterpriseAuthenticationService;
import com.rent.service.EnterpriseService;
import com.rent.service.UtilsService;
import com.rent.util.MyUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author obuivy
 */
@RestController
@RequestMapping("/manager/enterprise")
public class EntpSelfInfoController {
    @Autowired
    EnterpriseAuthenticationService enterpriseAuthenticationService;
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    UtilsService utilsService;
    @Autowired
    ContactMapper contactMapper;

    @RequestMapping("/updateSelfInfo")
    public ReturnMsg updateSelfInfo(@RequestBody Enterprise enterprise){
        if(!ShiroUtil.isAuthenticed()){
            return new ReturnMsg("30101",true,"尚未登录");
        }
        if(!ShiroUtil.hasRoles("enterprise_manager")){
            return new ReturnMsg("30201",true,"尚未授权");
        }
        if(enterprise.getEntpName().length() > 128){
            return new ReturnMsg("40205",true,"公司名过长");
        }
        if(enterprise.getEntpName().length() < 2){
            return new ReturnMsg("40206",true,"公司名过短");
        }
        if(enterprise.getEntpShopName().length() > 32){
            return new ReturnMsg("40207",true,"店铺名称过长");
        }
        if(enterprise.getEntpShopName().length() < 2){
            return new ReturnMsg("40208",true,"店铺名称过短");
        }
        if(utilsService.getThosePictures("picture_id",enterprise.getEntpPictureId()).size() == 0){
            return new ReturnMsg("40303",true,"该图组不存在");
        }
        if(enterpriseService.updateSelfInfo(enterprise)){
            return new ReturnMsg("200",false,"修改成功");
        }else {
            return new ReturnMsg("500",true,"修改失败");
        }
    }

    @RequestMapping("/setContact")
    public ReturnMsg setContact(@RequestBody Contact contact){
        if(!ShiroUtil.isAuthenticed()){
            return new ReturnMsg("30101",true,"尚未登录");
        }
        if(!ShiroUtil.hasRoles("enterprise_manager")){
            return new ReturnMsg("30201",true,"尚未授权");
        }
        if(MyUtil.strHasVoid(
                contact.getContactAddress(),
                contact.getContactName(),
                contact.getContactPhone())
        ){
            return new ReturnMsg("40101",true,"传参不齐");
        }
        if(contact.getContactAddress().length() > 256){
            return new ReturnMsg("40201",true,"收货地址过长");
        }
        if(contact.getContactAddress().length() < 2){
            return new ReturnMsg("40202",true,"收货地址过短");
        }
        if(!MyUtil.isPhoneNumber(contact.getContactPhone())){
            return new ReturnMsg("40203",true,"不是合法手机号");
        }
        if(contact.getContactName().length() > 16){
            return new ReturnMsg("40201",true,"收件人姓名过长");
        }
        if(contact.getContactName().length() < 2){
            return new ReturnMsg("40202",true,"收件人姓名过短");
        }
        contact.setContactReceiveId("E" + enterpriseService.getThoseEnterprises("entp_account",
                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId());
        try {
            QueryWrapper<Contact> queryWrapper = new QueryWrapper<>();
            queryWrapper.eq("contact_receive_id",contact.getContactReceiveId());
            if (contactMapper.selectList(queryWrapper).size() != 0){
                contactMapper.update(contact,queryWrapper);
            }else {
                contactMapper.insert(contact);
            }
            return new ReturnMsg("20001",false,"设置成功");
        } catch (Exception e) {
            return new ReturnMsg("50001",true,"服务器插入数据出错");
        }
    }
    @RequestMapping("/getContact")
    public ReturnMsg getContact() {
        if(!ShiroUtil.isAuthenticed()){
            return new ReturnMsg("30101",true,"尚未登录");
        }
        if(!ShiroUtil.hasRoles("enterprise_manager")){
            return new ReturnMsg("30201",true,"尚未授权");
        }
        return new ReturnMsg("200",false,"获取成功", contactMapper.selectList(
                new QueryWrapper<Contact>().eq("contact_receive_id",
                        "E"+ enterpriseService.getThoseEnterprises("entp_account",
                                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId())));
    }

}
