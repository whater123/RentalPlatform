package com.rent.pojo.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author MSI-PC
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderLogistics {
    @TableId(type = IdType.AUTO)
    int logisticsId;
    String entpToUserNumber;
    String entpToUserCompany;
    String userToEntpNumber;
    String userToEntpCompany;
    @TableField(exist = false)
    String orderId;
}
