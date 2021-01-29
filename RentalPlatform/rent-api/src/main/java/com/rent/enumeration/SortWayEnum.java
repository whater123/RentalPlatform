package com.rent.enumeration;

/**
 *排序方法的枚举类
 * @author w
 */
public enum SortWayEnum {
    /**
     *
     */
    DEFAULT(1,"推荐排序"),
    SALESVOLUME_DESC(2,"销量降序"),
    SALESVOLUME_ASC(3,"销量升序"),
    PRICE_DESC(4,"价格降序"),
    PRICE_ASC(5,"价格升序");

    private int code;
    private String name;

    SortWayEnum(int code, String name) {
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
