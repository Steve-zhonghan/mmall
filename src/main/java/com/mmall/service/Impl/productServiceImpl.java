package com.mmall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Lists;
import com.mmall.common.Consts;
import com.mmall.common.responseCode;
import com.mmall.common.serverResponse;
import com.mmall.dao.CategoryMapper;
import com.mmall.dao.ProductMapper;
import com.mmall.pojo.Category;
import com.mmall.pojo.Product;
import com.mmall.service.ICategoryService;
import com.mmall.service.IProductService;
import com.mmall.utility.DateTimeUtil;
import com.mmall.utility.PropertiesUtil;
import com.mmall.vo.productDetailVo;
import com.mmall.vo.productListVo;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service("iProductService")
public class productServiceImpl implements IProductService {

    @Autowired
    private ProductMapper productMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private ICategoryService iCategoryService;


    @Override
    public serverResponse saveOrUpdateProduct(Product product) {
        if (product != null) {
            if (StringUtils.isNotBlank(product.getSubImages())) {
                String[] subImageArray = product.getSubImages().split(",");
                if (subImageArray.length > 0) {
                    product.setMainImage(subImageArray[0]);
                }
                if (product.getId() != null) {
                    int rowCount = productMapper.updateByPrimaryKey(product);
                    if (rowCount > 0) {
                        return serverResponse.createBySuccess("Succeed to update product");
                    }
                    return serverResponse.createByErrorMessage("Failed to update product");
                } else {
                    int rowCount = productMapper.insert(product);
                    if (rowCount > 0) {
                        return serverResponse.createBySuccess("Succeed to add product");
                    }
                    return serverResponse.createByErrorMessage("Failed to add product");
                }
            }
        }
        return serverResponse.createByErrorMessage("Wrong parameter");
    }

    @Override
    public serverResponse<String> setSaleStatus(Integer productId,Integer status){
        if(productId == null|| status==null){
            return serverResponse.createByErrorCodeMessage(responseCode.ILLEGAL_ARGUMENT.getCode(),responseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = new Product();
        product.setId(productId);
        product.setStatus(status);
        int rowCount = productMapper.updateByPrimaryKeySelective(product);
        if(rowCount>0){
            return serverResponse.createBySuccess("Succeed to modify product status");
        }
        return serverResponse.createByErrorMessage("Failed to modify product status");
    }

    @Override
    public serverResponse<productDetailVo> manageProductDetail(Integer productId){
        if(productId == null){
            return serverResponse.createByErrorCodeMessage(responseCode.ILLEGAL_ARGUMENT.getCode(),responseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return serverResponse.createByErrorMessage("The product is deleted or sold out");
        }
        //VO（value object）对象
        productDetailVo productDetailVo = assembleProductDetailVo(product);
        return serverResponse.createBySuccess(productDetailVo);
    }

    private productDetailVo assembleProductDetailVo(Product product){
        productDetailVo productDetailVo = new productDetailVo();
        productDetailVo.setId(product.getId());
        productDetailVo.setSubtitle(product.getSubtitle());
        productDetailVo.setPrice(product.getPrice());
        productDetailVo.setMainImage(product.getMainImage());
        productDetailVo.setSubImage(product.getSubImages());
        productDetailVo.setCategoryId(product.getCategoryId());
        productDetailVo .setDetail(product.getDetail());
        productDetailVo.setName(product.getName());
        productDetailVo.setStatus(product.getStatus());
        productDetailVo.setStock(product.getStock());

        //properties file to get image host url
        productDetailVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.imooc.com/"));
        Category category = categoryMapper.selectByPrimaryKey(product.getCategoryId());
        if(category == null){
            productDetailVo.setParentCategoryId(0);
        }else{
            productDetailVo.setParentCategoryId(category.getParentId());
        }

        //DateTime util to convert the format of time in mybatis to other
        productDetailVo.setCreateTime(DateTimeUtil.dateToStr(product.getCreateTime()));
        productDetailVo.setUpdateTime(DateTimeUtil.dateToStr(product.getUpdateTime()));
        return productDetailVo;
    }

    /**
     * mybatis pageHelper配置
     * @param pageNum
     * @param pageSize
     * @return
     */
    @Override
    public serverResponse<PageInfo> getProductList(int pageNum, int pageSize){
        List<Product> productList = productMapper.selectList();
        return serverResponse.createBySuccess(pageHelper(productList,pageNum,pageSize));
    }

    private productListVo assembleProductListVo(Product product){
        productListVo productListVo = new productListVo();
        productListVo.setId(product.getId());
        productListVo.setName(product.getName());
        productListVo.setCategoryId(product.getCategoryId());
        productListVo.setImageHost(PropertiesUtil.getProperty("ftp.server.http.prefix","http://image.imooc.com/"));
        productListVo.setMainImage(product.getMainImage());
        productListVo.setPrice(product.getPrice());
        productListVo.setSubtitle(product.getSubtitle());
        productListVo.setStatus(product.getStatus());

        return productListVo;
    }

    @Override
    public serverResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize){
        if(StringUtils.isNotBlank(productName)){
            productName = new StringBuilder().append("%").append(productName).append("%").toString();
        }
        List<Product> productList = productMapper.selectByNameAndProductId(productName,productId);

        return serverResponse.createBySuccess(pageHelper(productList,pageNum,pageSize));
    }

    public PageInfo pageHelper(List<Product> productList,int pageNum,int pageSize){
        //startPage
        PageHelper.startPage(pageNum,pageSize);
        List<productListVo> productListVoList = Lists.newArrayList();
        //自己的sql查询语句结果productList，然后pojo转成vo
        for(Product productItem:productList){
            productListVo productListVo = assembleProductListVo(productItem);
            productListVoList.add(productListVo);
        }
        //pageHelper收尾
        PageInfo pageResult = new PageInfo(productList);
        //重置
        pageResult.setList(productListVoList);
        return pageResult;
    }

    @Override
    public serverResponse<productDetailVo> getProductDetail(Integer productId){
        if(productId == null){
            return serverResponse.createByErrorCodeMessage(responseCode.ILLEGAL_ARGUMENT.getCode(),responseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        Product product = productMapper.selectByPrimaryKey(productId);
        if(product == null){
            return serverResponse.createByErrorMessage("The product is deleted or sold out");
        }
        if(product.getStatus() != Consts.ProductStatusEnum.ON_SALE.getCode()){
            return serverResponse.createByErrorMessage("The product is deleted or sold out");
        }
        //VO（value object）对象
        productDetailVo productDetailVo = assembleProductDetailVo(product);
        return serverResponse.createBySuccess(productDetailVo);
    }
    @Override
    public serverResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy){
        if(StringUtils.isBlank(keyword) && categoryId == null){
            return serverResponse.createByErrorCodeMessage(responseCode.ILLEGAL_ARGUMENT.getCode(),responseCode.ILLEGAL_ARGUMENT.getDesc());
        }
        List<Integer> categoryIdList = new ArrayList<Integer>();
        if(categoryId != null) {
            Category category = categoryMapper.selectByPrimaryKey(categoryId);
            if (category == null && StringUtils.isBlank(keyword)) {
                //没有该分类 并且 没有关键字，返回空结果集 不报错
                PageHelper.startPage(pageNum, pageSize);
                List<productListVo> productListVoList = Lists.newArrayList();
                PageInfo pageInfo = new PageInfo(productListVoList);
                return serverResponse.createBySuccess(pageInfo);
            }
            categoryIdList = iCategoryService.selectCategoryAndChildrenById(category.getId()).getData();
        }
        if(StringUtils.isNotBlank(keyword)){
            keyword = new StringBuilder().append("%").append(keyword).append("%").toString();
        }
        PageHelper.startPage(pageNum,pageSize);
        //排序处理
        if(StringUtils.isNotBlank(orderBy)){
            if(Consts.ProductListOrderBy.PRICE_ASC_DESC.contains(orderBy)){
                String[] orderByArray = orderBy.split("_");
                PageHelper.orderBy(orderByArray[0]+" "+orderByArray[1]);
            }
        }
        List<Product> productList = productMapper.selectByNameAndCategoryIds(StringUtils.isBlank(keyword)?null:keyword,categoryIdList.size()==0?null:categoryIdList);
        List<productListVo> productListVoList = Lists.newArrayList();
        for(Product product :productList){
            productListVo productListVo1 = assembleProductListVo(product);
            productListVoList.add(productListVo1);
        }
        PageInfo pageInfo = new PageInfo(productList);
        pageInfo.setList(productListVoList);
        return serverResponse.createBySuccess(pageInfo);
    }
}
