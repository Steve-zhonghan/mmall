package com.mmall.service;

import com.github.pagehelper.PageInfo;
import com.mmall.common.serverResponse;
import com.mmall.pojo.Shipping;

public interface IShippingService {
    serverResponse add(Integer userId, Shipping shipping);
    serverResponse<String> del(Integer userId,Integer shippingId);
    serverResponse update(Integer userId, Shipping shipping);
    serverResponse<Shipping> select(Integer userId, Integer shippingId);
    serverResponse<PageInfo> list(Integer userId, int pageNum, int pageSize);
}
