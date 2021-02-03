package com.rent.memoryBase;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @author w
 */
public class UserWatchedFilter {
    /**
     * 用于存储用户近几分钟（取决于定时配置）内以及推荐过的商品id
     */
    static public Map<String, Set<Integer>> USER_WATCHED = new HashMap<>();
}
