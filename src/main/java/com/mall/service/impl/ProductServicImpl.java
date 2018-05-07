package com.mall.service.impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mall.common.Const;
import com.mall.common.ResponseCode;
import com.mall.common.ServerResponse;
import com.mall.dao.CategoryMapper;
import com.mall.dao.ProductMapper;
import com.mall.pojo.Category;
import com.mall.pojo.Product;
import com.mall.service.ICategoryService;
import com.mall.service.IProductService;
import com.mall.util.DateTimeUtil;
import com.mall.util.PropertiesUtil;
import com.mall.vo.ProductDetailVo;
import com.mall.vo.ProductListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wxhong on 2018/5/3.
 */
@Service("iProductService")
public class ProductServicImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;
    @Autowired
    private CategoryMapper categoryMapper;
    @Autowired
    private ICategoryService iCategoryService;

    @Override
    public ServerResponse SaveOrUpdateProduct(Product product){
        if(product != null){
            if(StringUtils.isNotBlank(product.getSubImages())){
                //如果子图不为空，则取第一个子图复制给主图
                String[] subImageArray =  product.getSubImages().split(",");
                if(subImageArray.length > 0){
                    product.setMainImage(subImageArray[0]);
                }
            }
            //此处有bug,参数不全的时候会导致sql有问题
            int rowCount = 0;
            if(product.getId() != null){
                rowCount =  productMapper.updateByPrimaryKey(product);
                if(rowCount > 0)
                    return ServerResponse.createBySuccessMessage("更新产品成功");
                else
                    return ServerResponse.createByErrorMessage("更新产品失败");
            }else{
                rowCount = productMapper.insert(product);
                if(rowCount > 0)
                    return ServerResponse.createBySuccessMessage("新增产品成功");
                else
                    return ServerResponse.createByErrorMessage("新增产品失败");
            }
        }

        return ServerResponse.createByErrorMessage("新增或更新产品参数不正确");
    }

    @Override
    public ServerResponse<String> setSaleStatus(Integer productId, Integer status) {
        if(productId == null || status == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);

        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount > 0){
            return ServerResponse.createByErrorMessage("更新状态成功");
        }

        return ServerResponse.createByErrorMessage("更新状态失败");
    }

    @Override
    public ServerResponse<ProductDetailVo> manageProductDetail(Integer productId) {
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        // -- vo对象
        //pojo --> bo --> vo
        ProductDetailVo productDetailVo = assmbleProductDetailVo(product);

        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResponse<PageInfo> getProductList(int pageNum, int pageSize) {
        //StartPage--start
        //填充自己的sql查询逻辑
        //pageHelper-收尾
        PageHelper.startPage(pageNum, pageSize);
        List<Product> productList = productMapper.selectList();

        List<ProductDetailVo> productDetailVoList = Lists.newArrayList();
        for(Product productItem:productList){
            ProductDetailVo productListVo = assmbleProductDetailVo(productItem);
            productDetailVoList.add(productListVo);
        }

        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productDetailVoList);

        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<PageInfo> searchProduct(String productName, Integer productId, int pageNum, int pageSize) {
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName, productId);

        List<ProductDetailVo> productDetailVoList = Lists.newArrayList();
        for(Product productItem:productList){
            ProductDetailVo productListVo = assmbleProductDetailVo(productItem);
            productDetailVoList.add(productListVo);
        }

        PageInfo pageResult = new PageInfo(productList);
        pageResult.setList(productDetailVoList);
        return ServerResponse.createBySuccess(pageResult);
    }

    @Override
    public ServerResponse<ProductDetailVo> getProductDetail(Integer productId) {
        if(productId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }
        // -- vo对象
        //pojo --> bo --> vo
        if(product.getStatus() != Const.ProductStatusEnum.ON_SALE.getCode()){
            return ServerResponse.createByErrorMessage("产品已下架或者删除");
        }

        ProductDetailVo productDetailVo = assmbleProductDetailVo(product);
        return ServerResponse.createBySuccess(productDetailVo);
    }

    @Override
    public ServerResponse<PageInfo> getProductByKeywordCategory(String keyword, Integer categoryId, int pageNum, int pageSize,String orderBy) {
        if(StringUtils.isBlank(keyword) && categoryId == null){
            return ServerResponse.createByErrorCodeMessage(ResponseCode.ILLEGAL_ARGUMENT.getCode(), ResponseCode.ILLEGAL_ARGUMENT.getDesc());
        }

        List<Integer> categoryIdList = new ArrayList<>();

        if(categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                //没有该分类，并且没有关键字这个时候返回一个空的结构体，不报错
                PageHelper.startPage(pageNum, pageSize);
                List<ProductListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return ServerResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(categoryId).getData();
        }
         if(StringUtils.isNotBlank(keyword)){
             keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
         }

         PageHelper.startPage(pageNum,pageSize);
         //排序处理
         if(StringUtils.isNotBlank(orderBy)){
             if(Const.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy))
             {
                 String[] orderByArray = orderBy.split("_");
                 PageHelper.orderBy(orderByArray[0] + " " + orderByArray[1]);
             }
         }
         List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword, categoryIdList.size() == 0?null:categoryIdList);
         List<ProductListVo> productListVoList = Lists.newArrayList();
         for(Product productItem:productList){
             productListVoList.add(assmbleProductListVo(productItem));
         }
         PageInfo pageInfo = new PageInfo(productList);
         pageInfo.setList(productListVoList);

         return ServerResponse.createBySuccess(pageInfo);
    }

    private ProductDetailVo assmbleProductDetailVo(Product product){
        ProductDetailVo productDetailVo = new ProductDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImage(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo.setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        //imageHost
        //parentCategoryId
        //CreateTime
        //updateTime
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.itest.com/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category != null){
            productDetailVo.setParentCategoryId(category.getParentId());
        }else{
            productDetailVo.setParentCategoryId(0);
        }

        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));

         return productDetailVo;
    }

    private ProductListVo assmbleProductListVo(Product product){
        ProductListVo productListVo = new ProductListVo();

        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(product.getMainImage());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix", "http://img.itest.com/"));
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());
        return productListVo;
    }
}
