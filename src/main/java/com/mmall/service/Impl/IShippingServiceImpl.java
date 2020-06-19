package com.mmall.service.Impl;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.google.common.collect.Maps;
import com.mmall.common.serverResponse;
import com.mmall.dao.ShippingMapper;
import com.mmall.pojo.Shipping;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("iShippingService")
public class IShippingServiceImpl implements IShippingService {

    @Autowired
    private ShippingMapper shippingMapper;

    @Override
    public serverResponse add(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.insert(shipping);
        if(rowCount > 0){
            Map result = Maps.newHashMap();
            result.put("shippingId",shipping.getId());
            return serverResponse.createBySuccess("Succeed to create address",result);
        }
        return serverResponse.createByErrorMessage("Failed to create address");
    }

    @Override
    public serverResponse<String> del(Integer userId,Integer shippingId){
        int resultCount = shippingMapper.deleterByShippingIdUserId(userId,shippingId);
        if(resultCount > 0){
            return serverResponse.createBySuccess("Succeed to delete address");
        }
        return serverResponse.createByErrorMessage("Failed to delete address");
    }

    @Override
    public serverResponse update(Integer userId, Shipping shipping){
        shipping.setUserId(userId);
        int rowCount = shippingMapper.updateByShipping(shipping);
        if(rowCount > 0){
            return serverResponse.createBySuccess("Succeed to update address");
        }
        return serverResponse.createByErrorMessage("Failed to update address");
    }

    @Override
    public serverResponse<Shipping> select(Integer userId, Integer shippingId){
        Shipping shipping = shippingMapper.selectByShippingIdUserId(userId,shippingId);
        if(shipping == null){
            return serverResponse.createByErrorMessage("Failed to find address");
        }
        return serverResponse.createBySuccess("Succeed to find address",shipping);
    }
    public serverResponse<PageInfo> list(Integer userId, int pageNum, int pageSize){
        PageHelper.startPage(pageNum,pageSize);
        List<Shipping> shippingList = shippingMapper.selectByUserId(userId);
        PageInfo pageInfo = new PageInfo(shippingList);
        return serverResponse.createBySuccess(pageInfo);
    }
}
