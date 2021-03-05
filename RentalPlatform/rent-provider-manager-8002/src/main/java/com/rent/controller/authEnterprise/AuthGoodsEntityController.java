
package com.rent.controller.authEnterprise;

import com.rent.config.ShiroUtil;
import com.rent.pojo.base.EnterpriseGoods;
import com.rent.pojo.base.EnterpriseGoodsEntity;
import com.rent.pojo.view.ReturnMsg;
import com.rent.service.EnterpriseCategoryService;
import com.rent.service.EnterpriseService;
import com.rent.service.EntpGoodsService;
import com.rent.service.UtilsService;
import com.rent.util.MoneyUtil;
import com.rent.util.MyUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/manager/authEnterprise")
public class AuthGoodsEntityController {
    @Autowired
    EnterpriseService enterpriseService;
    @Autowired
    EntpGoodsService entpGoodsService;
    @Autowired
    UtilsService utilsService;
    @Autowired
    EnterpriseCategoryService enterpriseCategoryService;

    @RequestMapping(value = "/addGoodsEntity", produces = "application/json;charset=UTF-8")
    public ReturnMsg addGoodsEntity(@RequestBody EnterpriseGoodsEntity goodsEntity) throws Exception {
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("301", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("302", true, "尚未授权");
        }
        try {
            if (!utilsService.isNowEntpId(entpGoodsService.getThoseGoods("goods_id",
                    String.valueOf(goodsEntity.getGoodsId())).get(0).getEntpId())) {
                return new ReturnMsg("302", true, "尚未授权");
            }
        } catch (Exception e) {
            return new ReturnMsg("403", true, "该商品集不存在");
        }
        if (MyUtil.strHasVoid(
                goodsEntity.getGoodsRegularUnit(),
                goodsEntity.getGoodsCurrentPrice(),
                goodsEntity.getGoodsDeposit(),
                goodsEntity.getGoodsPrice(),
                goodsEntity.getGoodsRegularPrice())
        ) {
            return new ReturnMsg("401", true, "传参不齐");
        }
        goodsEntity.setGoodsCurrentPrice(MoneyUtil.addTail(goodsEntity.getGoodsCurrentPrice()));
        goodsEntity.setGoodsDeposit(MoneyUtil.addTail(goodsEntity.getGoodsDeposit()));
        goodsEntity.setGoodsPrice(MoneyUtil.addTail(goodsEntity.getGoodsPrice()));
        if (MyUtil.intHasVoid(
                goodsEntity.getAddNumber(),
                goodsEntity.getGoodsId(),
                goodsEntity.getGoodsRentExpert(),
                goodsEntity.getGoodsRentWay())
        ) {
            return new ReturnMsg("401", true, "传参不齐");
        }
        if (!MyUtil.isAllRuleMoney(
                goodsEntity.getGoodsCurrentPrice(),
                goodsEntity.getGoodsDeposit(),
                goodsEntity.getGoodsPrice())
        ) {
            return new ReturnMsg("402", true, "传参不合法");
        }
        if (!entpGoodsService.isMatchedProperties(entpGoodsService.
                getGoodsAttribute(goodsEntity.getGoodsId()), goodsEntity.getProperties())
        ) {
            return new ReturnMsg("403", true, "属性不匹配");
        }
        if (entpGoodsService.insertGoodsEntity(goodsEntity)) {
            return new ReturnMsg("200", false, "添加成功");
        } else {
            return new ReturnMsg("500", true, "添加失败");
        }
    }

    @RequestMapping(value = "/getGoodsEntities", produces = "application/json;charset=UTF-8")
    public ReturnMsg getGoodsEntities(@RequestBody EnterpriseGoods goods) throws Exception {
        if (!ShiroUtil.isAuthenticed()) {
            return new ReturnMsg("301", true, "尚未登录");
        }
        if (!ShiroUtil.hasRoles("authEnterprise_manager")) {
            return new ReturnMsg("302", true, "尚未授权");
        }
        try {
            if (!utilsService.isNowEntpId(entpGoodsService.getThoseGoods("goods_id",
                    String.valueOf(goods.getGoodsId())).get(0).getEntpId())) {
                return new ReturnMsg("302", true, "尚未授权");
            }
        } catch (Exception e) {
            return new ReturnMsg("403", true, "该商品集不存在");
        }
        return new ReturnMsg("200",false,"获取成功",
                entpGoodsService.getThoseGoodsEntity("goods_id",String.valueOf(goods.getGoodsId())));
    }
}
