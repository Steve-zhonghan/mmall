package com.mmall.dao;

import com.mmall.pojo.OrderItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface OrderItemMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(OrderItem record);

    int insertSelective(OrderItem record);

    OrderItem selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(OrderItem record);

    int updateByPrimaryKey(OrderItem record);

    List<OrderItem> getByOrderNoUserId(@Param("OrderNo")Long orderNo,@Param("userId") Integer userId);

    void batchInsert(@Param("orderList")List<OrderItem> orderList);

    List<OrderItem> getByOrderNo(@Param("OrderNo")Long orderNo);

}