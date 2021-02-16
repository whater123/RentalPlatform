package com.rent.pojo.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author w
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLogistics {
    @TableId(type = IdType.AUTO)
    private int logisticsId;
    private String entpToUserNumber;
    private String entpToUserCompany;
    private String userToEntpNumber;
    private String userToEntpCompany;
}
