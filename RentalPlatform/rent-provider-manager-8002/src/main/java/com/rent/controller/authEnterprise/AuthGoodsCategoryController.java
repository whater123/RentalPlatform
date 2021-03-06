package com.rent.controller.authEnterprise;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rent.config.ShiroUtil;
import com.rent.pojo.base.EnterpriseCategory;
import com.rent.pojo.base.manager.EnterpriseGoods;
import com.rent.pojo.base.manager.EnterpriseGoodsEntity;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.EnterpriseCategoryService;
import com.rent.service.EnterpriseService;
import com.rent.service.EntpGoodsService;
import com.rent.service.UtilsService;
import com.rent.util.MoneyUtil;
import com.rent.util.MyUtil;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author MSI-PC
 */
@RestController
@RequestMapping("/manager/authEnterprise")
public class AuthGoodsCategoryController {
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    EntpGoodsService entpGoodsService;
    @Autowired
    UtilsService utilsService;
    @Autowired
    EnterpriseCategoryService enterpriseCategoryService;

    @RequestMapping(value = "/category/{key}", produces = "application/json;charset=UTF-8")
    public ReturnMsg category(@RequestBody EnterpriseCategory enterpriseCategory, @PathVariable String key) {
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("30101", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("30201", true, "尚未授权");
        }
        enterpriseCategory.setEntpId(enterpriseService.getThoseEnterprises("entp_account",
                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId());
        if ("insert".equals(key)) {
            if (MyUtil.strHasVoid(enterpriseCategory.getCategoryName())) {
                return new ReturnMsg("40101", true, "插入传参不齐");
            }
            if (MyUtil.intHasVoid(enterpriseCategory.getParentId())) {
                return new ReturnMsg("40101", true, "插入传参不齐");
            }
            if (enterpriseCategoryService.getThoseEnterpriseCategories
                    ("category_id", String.valueOf(enterpriseCategory.getParentId())).size() == 0) {
                return new ReturnMsg("40303", false, "父类id不存在");
            }
            enterpriseCategory.setCategoryId(0);
            if (enterpriseCategoryService.insertCategory(enterpriseCategory)) {
                return new ReturnMsg("20001", false, "添加成功");
            } else {
                return new ReturnMsg("50001", false, "添加失败");
            }
        } else if ("delete".equals(key)) {
            if (MyUtil.intHasVoid(enterpriseCategory.getCategoryId())) {
                return new ReturnMsg("40102", true, "删除传参不齐");
            }
            if (enterpriseCategoryService.getThoseEnterpriseCategories
                    ("category_id", String.valueOf(enterpriseCategory.getCategoryId())).size() == 0) {
                return new ReturnMsg("40301", false, "删除目标不存在");
            }
            if (!utilsService.isNowEntpId(enterpriseCategoryService.
                    getThoseEnterpriseCategories("category_id",
                            String.valueOf(enterpriseCategory.getCategoryId())).get(0).getEntpId())) {
                return new ReturnMsg("30202", true, "尚未授权");
            }
            if (enterpriseCategoryService.deleteCategory(enterpriseCategory)) {
                return new ReturnMsg("20002", false, "删除成功");
            } else {
                return new ReturnMsg("50002", false, "删除失败");
            }
        } else if ("update".equals(key)) {
            if (MyUtil.strHasVoid(enterpriseCategory.getCategoryName()) &&
                    MyUtil.intHasVoid(enterpriseCategory.getParentId())) {
                return new ReturnMsg("40103", true, "修改传参不齐");
            }
            if (MyUtil.intHasVoid(enterpriseCategory.getCategoryId())) {
                return new ReturnMsg("40103", true, "修改传参不齐");
            }
            if (enterpriseCategoryService.getThoseEnterpriseCategories
                    ("category_id", String.valueOf(enterpriseCategory.getCategoryId())).size() == 0) {
                return new ReturnMsg("40302", false, "修改目标不存在");
            }
            if (!utilsService.isNowEntpId(enterpriseCategoryService.
                    getThoseEnterpriseCategories("category_id",
                            String.valueOf(enterpriseCategory.getCategoryId())).get(0).getEntpId())) {
                return new ReturnMsg("30203", true, "尚未授权");
            }

            if (enterpriseCategoryService.getThoseEnterpriseCategories
                    ("category_id", String.valueOf(enterpriseCategory.getParentId())).size() == 0) {
                return new ReturnMsg("40303", false, "父类id不存在");
            }
            if (!utilsService.isNowEntpId(enterpriseCategory.getEntpId())) {
                return new ReturnMsg("30203", true, "尚未授权");
            }
            if (enterpriseCategoryService.updateCategory(enterpriseCategory)) {
                return new ReturnMsg("20003", false, "修改成功");
            } else {
                return new ReturnMsg("50003", false, "修改失败");
            }
        } else {
            return new ReturnMsg("40201", false, "请求参数错误");
        }
    }

    @RequestMapping(value = "/getCategory", produces = "application/json;charset=UTF-8")
    public ReturnMsg getCategory() {
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("301", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("302", true, "尚未授权");
        }
        return new ReturnMsg("200", false, "获取成功",
                enterpriseCategoryService.getThoseEnterpriseCategories("entp_id",
                        String.valueOf(enterpriseService.getThoseEnterprises("entp_account",
                                String.valueOf(SecurityUtils.getSubject().getPrincipal())).get(0).getEntpId())));

    }
}
