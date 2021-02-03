package com.rent.service.impl;

import com.rent.dao.EnterpriseMapper;
import com.rent.pojo.base.Enterprise;

import java.util.List;
/**
 * @author obuivy
 */
public interface EnterpriseImpl {

    /**
     * @return 返回EnterpriseMapper
     */
    EnterpriseMapper getMapper();

    /**
     * @param that 数据库的属性
     * @param value 值
     * @return 数据库中column项为value的数据是否存在
     */
    boolean isThatExist(String that, String value);

    /**
     * @param column 数据库的属性
     * @param value 值
     * @return 返回数据库中column项为value的所有值
     */
    List<Enterprise> getThoseEnterprises(String column, String value);

    /**
     * @param userPhone
     * @param randomCode
     * @return
     */
    boolean userCheckVerification(String userPhone, String randomCode);
}
