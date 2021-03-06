package com.rent.pojo.base.manager;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Enterprise {
    @TableId(type = IdType.AUTO)
    int entpId;
    String entpName;
    String entpPassword;
    String entpAccount;
    String entpIntroduce;
    String entpShopName;
    String entpChargePhone;
    String entpPictureId;
    String entpAccountMoney;
    String entpRegisterTime;
    @TableField(exist = false)
    private String entpVerification;
}
