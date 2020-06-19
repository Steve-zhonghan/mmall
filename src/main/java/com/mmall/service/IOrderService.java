package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.serverResponse;
import com.mmall.vo.OrderVo;

import java.util.Map;

public interface IOrderService {

    serverResponse pay(Long orderNo, Integer userId, String path);
    serverResponse aliCallback(Map<String,String> params);
    serverResponse queryOrderPayStatus(Integer userId, Long orderNo);
    serverResponse createOrder(Integer userId,Integer shippingId);
    serverResponse<String> cancel(Integer userId,Long orderNo);
    serverResponse getOrderCartProduct(Integer userId);
    serverResponse<OrderVo> getOrderDetail(Integer userId, Long orderNo);
    serverResponse<PageInfo> getOrderList(Integer userId, int pageNum, int pageSize);

    //Backend
    serverResponse<PageInfo> manageList(int pageNum,int pageSize);
    serverResponse<OrderVo> manageDetail(Long orderNo);
    serverResponse<PageInfo> manageSearch(Long orderNo,int pageNum,int pageSize);
    serverResponse<String> manageSendGoods(Long orderNo);
}
