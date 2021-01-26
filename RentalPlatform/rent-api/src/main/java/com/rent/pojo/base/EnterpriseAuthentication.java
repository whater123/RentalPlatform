package com.rent.pojo.base;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EnterpriseAuthentication {
    @TableId(type = IdType.AUTO)
    int authId;
    int entpId;
    String authLicensePictureId;
    String authCardPictureId;
    String authEmail;
    int authState;
    String authAddress;
}
