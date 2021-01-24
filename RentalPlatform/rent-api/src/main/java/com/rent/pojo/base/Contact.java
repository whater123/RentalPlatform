package com.rent.pojo.base;

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
public class Contact {
    @TableId(type = IdType.AUTO)
    private int contactId;
    private String contactAddress;
    private int contactReceiveId;
    private String contactPhone;
    private String contactName;
}
