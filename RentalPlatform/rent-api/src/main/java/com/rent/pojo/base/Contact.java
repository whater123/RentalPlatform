package com.rent.pojo.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author w
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class Contact {
    @TableId(type = IdType.AUTO)
    private int contactId;
    private String contactAddress;
    private String contactReceiveId;
    private String contactPhone;
    private String contactName;

    public Contact(String contactAddress, String contactPhone, String contactName) {
        this.contactAddress = contactAddress;
        this.contactPhone = contactPhone;
        this.contactName = contactName;
    }
}
