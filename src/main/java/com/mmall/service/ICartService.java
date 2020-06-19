package com.mmall.service;

import com.mmall.common.serverResponse;
import com.mmall.vo.cartVo;

public interface ICartService {
    serverResponse<cartVo> add(Integer userId, Integer productId, Integer count);
    serverResponse<cartVo> update(Integer userId, Integer productId,Integer count);
    serverResponse<cartVo> deleteProduct(Integer userId,String productIds);
    serverResponse<cartVo> list(Integer userId);
    serverResponse<cartVo> selectOrUnSelect(Integer userId,Integer productId,Integer checked);
    serverResponse<Integer> getCartProductCount(Integer userId);
}
