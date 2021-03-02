package com.rent.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.rent.dao.EnterpriseCategoryMapper;
import com.rent.pojo.base.EnterpriseCategory;
import com.rent.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EnterpriseCategoryService {
    @Autowired
    EnterpriseCategoryMapper enterpriseCategoryMapper;
    @Autowired
    EnterpriseService enterpriseService;

    public List<EnterpriseCategory> getThoseEnterpriseCategories(String column, String value){
        QueryWrapper<EnterpriseCategory> queryWrapper = new QueryWrapper<EnterpriseCategory>();
        queryWrapper.eq(column,value);
        return enterpriseCategoryMapper.selectList(queryWrapper);
    }

    public boolean insertCategory(EnterpriseCategory newEnterpriseCategory){
        enterpriseCategoryMapper.insert(newEnterpriseCategory);
        return true;
    }

    public boolean deleteCategory(EnterpriseCategory oldEnterpriseCategory){
        QueryWrapper<EnterpriseCategory> queryWrapper = new QueryWrapper<EnterpriseCategory>();
        queryWrapper.eq("category_id", oldEnterpriseCategory.getCategoryId());
        enterpriseCategoryMapper.delete(queryWrapper);
        return true;
    }

    public boolean updateCategory(EnterpriseCategory newEnterpriseCategory){
        QueryWrapper<EnterpriseCategory> queryWrapper = new QueryWrapper<EnterpriseCategory>();
        queryWrapper.eq("category_id", newEnterpriseCategory.getCategoryId());
        EnterpriseCategory oldEnterpriseCategory = enterpriseCategoryMapper.selectList(queryWrapper).get(0);
        if(!MyUtil.strHasVoid(newEnterpriseCategory.getCategoryName())){
            oldEnterpriseCategory.setCategoryName(newEnterpriseCategory.getCategoryName());
        }
        if(!MyUtil.intHasVoid(newEnterpriseCategory.getParentId())){
            oldEnterpriseCategory.setParentId(newEnterpriseCategory.getParentId());
        }
        enterpriseCategoryMapper.update(oldEnterpriseCategory,queryWrapper);
        return true;
    }


}
