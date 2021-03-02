package com.rent.pojo.base;

import com.baomidou.mybatisplus.annotation.TableField;
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
public class Appeal {
    @TableId(value = "appeal_id")
    private int appealId;
    private int appealType;
    private String appealGoalId;
    private String appealCategory;
    private String appealContext;
    private String appealPictureId;
    private int appealState;
    private String appealTime;
    @TableField(exist = false)
    private User user;
    @TableField(exist = false)
    private Enterprise enterprise;
}
