package com.rent.controller;

import com.rent.dao.EnterpriseAuthenticationMapper;
import com.rent.dao.EnterpriseGoodsEntityMapper;
import com.rent.dao.EnterpriseMapper;
import com.rent.pojo.base.manager.Enterprise;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * @author obuivy
 */
@RestController
public class MyController {
    @Autowired
    EnterpriseAuthenticationMapper enterpriseAuthenticationMapper;
    @Autowired
    EnterpriseGoodsEntityMapper enterpriseGoodsEntityMapper;
    @Autowired
    EnterpriseMapper enterpriseService;

    @RequestMapping("/")
    public int rsndm(){
        List< Enterprise > list = enterpriseService.selectList(null);
        return list.size();
    }
}
