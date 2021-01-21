package com.rent.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rent.dao.EnterpriseMapper;
import com.rent.pojo.base.Enterprise;
import com.rent.pojo.view.ReturnMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EnterpriseService {
    @Autowired
    EnterpriseMapper enterpriseMapper;

    public EnterpriseMapper getEnterpriseMapper(){
        return enterpriseMapper;
    }

    public int toRegisiter(String json){
        return 0;
    }
}
