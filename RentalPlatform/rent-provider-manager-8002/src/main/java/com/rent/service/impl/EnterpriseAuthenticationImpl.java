package com.rent.service.impl;

import com.rent.dao.EnterpriseAuthenticationMapper;
import com.rent.pojo.base.EnterpriseAuthentication;

import java.util.List;

/**
 * @author obuivy
 */
public interface EnterpriseAuthenticationImpl {

    /**
     * @return 返回EnterpriseAuthenticationMapper
     */
    EnterpriseAuthenticationMapper getMapper();
    /**
     * @param column 数据库的属性
     * @param value 值
     * @return 返回数据库中column项为value的所有值
     */
    List<EnterpriseAuthentication> getThoseEnterpriseAuthentications(String column, String value);
}
