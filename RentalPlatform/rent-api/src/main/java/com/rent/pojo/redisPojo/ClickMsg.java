package com.rent.pojo.redisPojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * @author w
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ClickMsg implements Serializable {
    private int userId;
    private int goodsId;
    private int entpId;
    private String time;
}
