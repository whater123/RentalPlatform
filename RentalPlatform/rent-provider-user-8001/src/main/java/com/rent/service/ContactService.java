package com.rent.service;

import com.rent.pojo.base.user.Contact;

import java.util.List;

/**
 * @author w
 */
public interface ContactService {

    /**
     * 根据经纬度获取到详细位置
     * @param lat 经度
     * @param lng 纬度
     * @return 详细地点,失败则返回null
     */
    String userGetAddress(String lat,String lng);

    /**
     * 用户添加联系信息
     * @param contact 联系信息
     * @return 是否添加成功
     */
    boolean insertContact(Contact contact);

    /**
     * 根据U+id获取用户所有的联系信息
     * @param userIdAddU U+id
     * @return 所有的联系信息
     */
    List<Contact> getAllContact(String userIdAddU);

    /**
     * 根据联系信息id删除联系信息
     * @param contactId 联系信息id
     * @return 是否成功
     */
    boolean deleteContact(int contactId);

    /**
     * 根据id修改联系信息
     * @param contact 联系信息id和收货人id不变，其他信息改变的update对象
     * @return 是否成功
     */
    boolean updateContact(Contact contact);

    /**
     * 根据id获取联系信息
     * @param contactId 联系信息id
     * @return 处理后的联系信息
     */
    Contact getContactById(int contactId);
}
