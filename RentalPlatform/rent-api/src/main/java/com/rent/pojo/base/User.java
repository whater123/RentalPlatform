package com.rent.pojo.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author w
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class User {
    @TableId(type = IdType.AUTO)
    private int userId;
    private String userName;
    private String userPassword;
    private String userPictureId;
    private int userCreditScore;
    private String userPhone;
    private String userRealName;
    private String userIdNumber;
    private Date userRegisterTime;
    private int userAccountMoney;
    @TableField(exist = false)
    private String userVerification;
    @TableField(exist = false)
    private String userToken;

    public User(int userId) {
        this.userId = userId;
    }
}
