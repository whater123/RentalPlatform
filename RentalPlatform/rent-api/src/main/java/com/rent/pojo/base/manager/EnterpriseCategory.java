package com.rent.pojo.base.manager;

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
public class EnterpriseCategory {
    @TableId(type = IdType.AUTO)
    int categoryId;
    int entpId;
    String categoryName;
    int parentId;
}
