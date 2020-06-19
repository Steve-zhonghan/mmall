package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.serverResponse;
import com.mmall.pojo.Product;
import com.mmall.vo.productDetailVo;

public interface IProductService {
    serverResponse saveOrUpdateProduct(Product product);
    serverResponse<String> setSaleStatus(Integer productId,Integer status);
    serverResponse<productDetailVo> manageProductDetail(Integer productId);
    serverResponse<PageInfo> getProductList(int pageNum, int pageSize);
    serverResponse<PageInfo> searchProduct(String productName,Integer productId,int pageNum,int pageSize);
    serverResponse<productDetailVo> getProductDetail(Integer productId);
    serverResponse<PageInfo> getProductByKeywordCategory(String keyword,Integer categoryId,int pageNum,int pageSize,String orderBy);
}
