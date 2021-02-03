package com.rent.enumeration;

/**
 *
 * @author w
 */

public enum CategoryEnum {
    /**
     * 热门分类枚举类
     */
    Smart_devices(1,"智能设备"),
    Camera_equipment(2,"摄像设备"),
    Clothing(3,"服装"),
    Entertainment(4,"娱乐");

    private int code;
    private String name;

    CategoryEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
