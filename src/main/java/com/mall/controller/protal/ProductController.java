package com.mall.controller.protal;

import com.github.pagehelper.PageInfo;
import com.mall.common.ServerResponse;
import com.mall.service.IProductService;
import com.mall.vo.ProductDetailVo;
import com.sun.xml.internal.ws.api.ha.StickyFeature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by wxhong on 2018/5/4.
 */
@Controller
@RequestMapping("/product/")
public class ProductController {
    @Autowired
    private IProductService iProductService;

    /**
     * 获取商品详情，不包括下架的商品
     * @param productId
     * @return
     */
    @RequestMapping("detial.do")
    @ResponseBody
    public ServerResponse<ProductDetailVo> detail(Integer productId){

        return iProductService.getProductDetail(productId);
    }

    /**
     * 根据关键字进行搜索
     * @param keyword
     * @param categoryId
     * @param pageNum
     * @param pageSize
     * @param orderBy
     * @return
     */
    @RequestMapping("list.do")
    @ResponseBody
    public ServerResponse<PageInfo> list(@RequestParam(value = "keyword", required = false)String keyword,
                                         @RequestParam(value = "categoryId", required = false)Integer categoryId,
                                         @RequestParam(value = "pageNum", defaultValue = "1") int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10") int pageSize,
                                         @RequestParam(value = "orderBy", defaultValue = "")String orderBy){

        return iProductService.getProductByKeywordCategory(keyword,categoryId, pageNum, pageSize,orderBy);
    }

}
