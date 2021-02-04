package com.rent.pojo.base.user;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author w
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserComment {
    @TableId(type = IdType.AUTO)
    private int commentId;
    private int goodsId;
    private int userId;
    private String commentPictureId;
    private String commentContext;
    private int commentStar;
    @TableField(exist = false)
    private String userPictureId;
    @TableField(exist = false)
    private String userName;
}
