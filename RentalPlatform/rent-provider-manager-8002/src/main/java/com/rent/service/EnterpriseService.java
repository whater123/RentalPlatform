package com.rent.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.EnterpriseMapper;
import com.rent.pojo.base.Enterprise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnterpriseService {
    @Autowired
    EnterpriseMapper enterpriseMapper;

    public EnterpriseMapper getEnterpriseMapper(){
        return enterpriseMapper;
    }

    public boolean isThatExist(String that, String value){
        QueryWrapper<Enterprise> queryWrapper = new QueryWrapper<Enterprise>();
        queryWrapper.eq(that,value);
        List<Enterprise> enterpriseList = enterpriseMapper.selectList(queryWrapper);
        return enterpriseList.size() != 0;
    }

    public List<Enterprise> getThoseEnterprises(String column, String value){
        QueryWrapper<Enterprise> queryWrapper = new QueryWrapper<Enterprise>();
        queryWrapper.eq(column,value);
        return enterpriseMapper.selectList(queryWrapper);
    }


    public int toRegisiter(String json){
        return 0;
    }

}
