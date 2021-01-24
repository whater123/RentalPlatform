package com.rent.service;

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

}
