package com.rent.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.rent.pojo.base.EnterpriseAuthentication;
import org.apache.ibatis.annotations.Select;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

@Repository
public interface EnterpriseAuthenticationMapper extends BaseMapper<EnterpriseAuthentication> {
    @Select("SELECT\n" +
            "auth_id AS authId,\n" +
            "auth_state AS authState\n" +
            "FROM enterprise_authentication;")
    List<EnterpriseAuthentication> getSimpleAuthentications();
}
