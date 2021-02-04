package com.rent.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rent.constant.SystemConstant;
import com.rent.dao.EnterpriseAuthenticationMapper;
import com.rent.pojo.base.manager.EnterpriseAuthentication;
import com.rent.pojo.view.ReturnDoubleData;
import com.rent.service.impl.PlatformImpl;
import com.rent.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PlatformService implements PlatformImpl {
    @Autowired
    EnterpriseAuthenticationMapper enterpriseAuthenticationMapper;
    @Override
    public ReturnDoubleData getAuthentications(String json) {
        try{
            JSONObject jsonObject = JSON.parseObject(json);
            if (MyUtil.jsonHasVoid(json, "page")) {
                if (MyUtil.jsonHasVoid(json, "authState")) {
                    //全部消息
                    return new ReturnDoubleData("200", false, "获取成功",
                            enterpriseAuthenticationMapper.getSimpleAuthentications(), null);
                } else {
                    return new ReturnDoubleData("200",false,"获取成功",
                            this.function1(jsonObject.getString("authState")),null);
                }
            } else {
                if (MyUtil.jsonHasVoid(json, "authState")) {
                    return this.function2(jsonObject.getInteger("page"),
                            enterpriseAuthenticationMapper.getSimpleAuthentications());
                } else {
                    return this.function2(jsonObject.getInteger("page"), this.function1(jsonObject.getString("authState")));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ReturnDoubleData("500", true,"服务器错误");
        }
    }

    private List<EnterpriseAuthentication> function1(String authState){
        //有状态区别，全部消息
        List<EnterpriseAuthentication> simpleAuthentications
                = enterpriseAuthenticationMapper.getSimpleAuthentications();
        List<EnterpriseAuthentication> authentications = new ArrayList<EnterpriseAuthentication>();
        for (EnterpriseAuthentication ea :
                simpleAuthentications) {
            if (ea.getAuthState() == Integer.parseInt(authState)) {
                authentications.add(ea);
            }
        }
        return authentications;
    }
    private ReturnDoubleData function2(int page, List<EnterpriseAuthentication> simpleAuthentications){
        //按页数给消息
        ArrayList<Integer> pageList = new ArrayList<Integer>();
        //获取总页数和当前页数放入pagelist数组
        if(simpleAuthentications.size() == 0){
            pageList.add(0);
            pageList.add(1);
            return new ReturnDoubleData("200",false,"获取成功",
                    null,pageList);
        }

        if(simpleAuthentications.size() % SystemConstant.GET_AUTHENTICATIONS_PER_PAGE == 0){
            pageList.add(simpleAuthentications.size() / SystemConstant.GET_AUTHENTICATIONS_PER_PAGE);
        }else {
            pageList.add((simpleAuthentications.size() / SystemConstant.GET_AUTHENTICATIONS_PER_PAGE) + 1);
        }
        pageList.add(page);
        //获取当前页数下的authentications数组
        if(pageList.get(0) < page){
            return new ReturnDoubleData("400",true,"此页面不存在");
        }else {
            ArrayList<EnterpriseAuthentication> authentications = new ArrayList<EnterpriseAuthentication>();
            try{
                for (int i = (page - 1)*SystemConstant.GET_AUTHENTICATIONS_PER_PAGE;
                     i < (page - 1)*SystemConstant.GET_AUTHENTICATIONS_PER_PAGE
                             + SystemConstant.GET_AUTHENTICATIONS_PER_PAGE; i++) {
                    authentications.add(simpleAuthentications.get(i));
                }
            }catch (Exception ignored){}
            return new ReturnDoubleData("200",false,"获取成功",
                    authentications,pageList);
        }

    }
}
