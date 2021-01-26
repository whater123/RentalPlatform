package com.rent.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.EnterpriseAuthenticationMapper;
import com.rent.pojo.base.EnterpriseAuthentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnterpriseAuthenticationService {
    @Autowired
    EnterpriseAuthenticationMapper enterpriseAuthenticationMapper;

    public EnterpriseAuthenticationMapper getMapper(){
        return enterpriseAuthenticationMapper;
    }

    public List<EnterpriseAuthentication> getThoseEnterpriseAuthentications(String column, String value){
        QueryWrapper<EnterpriseAuthentication> queryWrapper = new QueryWrapper<EnterpriseAuthentication>();
        queryWrapper.eq(column,value);
        return enterpriseAuthenticationMapper.selectList(queryWrapper);
    }

}
