package com.rent.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.EnterpriseMapper;
import com.rent.pojo.base.manager.Enterprise;
import com.rent.util.MyUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

import java.util.List;
/**
 * @author obuivy
 */
@Service
public class EnterpriseService implements com.rent.service.impl.EnterpriseImpl {
    @Autowired
    EnterpriseMapper enterpriseMapper;
    @Autowired
    RedisTemplate<String, Object> redisTemplate;

    @Override
    public EnterpriseMapper getMapper(){
        return enterpriseMapper;
    }
    @Override
    public boolean isThatExist(String that, String value){
        QueryWrapper<Enterprise> queryWrapper = new QueryWrapper<Enterprise>();
        queryWrapper.eq(that,value);
        List<Enterprise> enterpriseList = enterpriseMapper.selectList(queryWrapper);
        return enterpriseList.size() != 0;
    }
    @Override
    public List<Enterprise> getThoseEnterprises(String column, String value){
        QueryWrapper<Enterprise> queryWrapper = new QueryWrapper<Enterprise>();
        queryWrapper.eq(column,value);
        return enterpriseMapper.selectList(queryWrapper);
    }
    @Override
    public boolean userCheckVerification(String userPhone, String randomCode) {
        ValueOperations<String, Object> opsForValue = redisTemplate.opsForValue();
        Object o = opsForValue.get(userPhone);
        if (o==null){
            return false;
        }
        boolean equals = randomCode.trim().equals(o);
        if (equals){
            redisTemplate.delete(userPhone);
            return true;
        }
        else {
            return false;
        }
    }

    public boolean updateSelfInfo(Enterprise enterprise){
        try{
            QueryWrapper<Enterprise> queryWrapper = new QueryWrapper<Enterprise>();

            queryWrapper.eq("entp_id",getThoseEnterprises("entp_account",
                    String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId());
            Enterprise oldEnterprise = getMapper().selectList(queryWrapper).get(0);

            if(MyUtil.strHasVoid(enterprise.getEntpName())) {
                oldEnterprise.setEntpName(enterprise.getEntpName());
            }
            if(MyUtil.strHasVoid(enterprise.getEntpPictureId())) {
                oldEnterprise.setEntpPictureId(enterprise.getEntpPictureId());
            }
            if(MyUtil.strHasVoid(enterprise.getEntpShopName())) {
                oldEnterprise.setEntpShopName(enterprise.getEntpShopName());
            }
            if(MyUtil.strHasVoid(enterprise.getEntpIntroduce())) {
                oldEnterprise.setEntpIntroduce(enterprise.getEntpIntroduce());
            }
            getMapper().update(oldEnterprise,queryWrapper);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
