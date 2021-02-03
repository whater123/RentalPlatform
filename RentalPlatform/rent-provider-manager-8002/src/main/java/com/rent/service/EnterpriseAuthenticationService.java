package com.rent.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.EnterpriseAuthenticationMapper;
import com.rent.pojo.base.EnterpriseAuthentication;
import com.rent.service.impl.EnterpriseAuthenticationImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * @author obuivy
 */
@Service
public class EnterpriseAuthenticationService implements EnterpriseAuthenticationImpl {
    @Autowired
    EnterpriseAuthenticationMapper enterpriseAuthenticationMapper;
    @Override
    public EnterpriseAuthenticationMapper getMapper(){
        return enterpriseAuthenticationMapper;
    }
    @Override
    public List<EnterpriseAuthentication> getThoseEnterpriseAuthentications(String column, String value){
        QueryWrapper<EnterpriseAuthentication> queryWrapper = new QueryWrapper<EnterpriseAuthentication>();
        queryWrapper.eq(column,value);
        return enterpriseAuthenticationMapper.selectList(queryWrapper);
    }

}
