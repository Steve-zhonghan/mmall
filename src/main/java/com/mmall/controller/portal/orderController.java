package com.mmall.controller.portal;

import com.alipay.api.AlipayApiException;
import com.alipay.api.internal.util.AlipaySignature;
import com.alipay.demo.trade.config.Configs;
import com.google.common.collect.Maps;
import com.mmall.common.Consts;
import com.mmall.common.responseCode;
import com.mmall.common.serverResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.utility.CookieUtil;
import com.mmall.utility.RedisShardedPoolUtil;
import com.mmall.utility.jsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.Map;

@Controller
@RequestMapping("/order/")
public class orderController {

    private static final Logger logger = LoggerFactory.getLogger(orderController.class);

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("create.do")
    @ResponseBody
    public serverResponse create(HttpServletRequest request, Integer shippingId){
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return serverResponse.createByErrorMessage("You need to login");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = jsonUtil.string2Obj(userJsonStr,User.class);
        if(user ==null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.createOrder(user.getId(),shippingId);
    }

    @RequestMapping("cancel.do")
    @ResponseBody
    public serverResponse cancel(HttpServletRequest request, Long orderNo){
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return serverResponse.createByErrorMessage("You need to login");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = jsonUtil.string2Obj(userJsonStr,User.class);
        if(user ==null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.cancel(user.getId(),orderNo);
    }

    @RequestMapping("get_order_Cart_product.do")
    @ResponseBody
    public serverResponse getOrderCartProduct(HttpServletRequest request){
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return serverResponse.createByErrorMessage("You need to login");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = jsonUtil.string2Obj(userJsonStr,User.class);
        if(user ==null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderCartProduct(user.getId());
    }



    @RequestMapping("pay.do")
    @ResponseBody
    public serverResponse pay(Long orderNo, HttpServletRequest request){
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return serverResponse.createByErrorMessage("You need to login");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = jsonUtil.string2Obj(userJsonStr,User.class);
        if(user ==null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        String path = request.getServletContext().getRealPath("upload");
        return iOrderService.pay(orderNo,user.getId(),path);
    }

    @RequestMapping("alipay_callback.do")
    @ResponseBody
    public Object alipayCallback(HttpServletRequest request){
        Map<String,String> params = Maps.newHashMap();
        Map requestParams = request.getParameterMap();
        for(Iterator iter = requestParams.keySet().iterator();iter.hasNext();){
            String name = (String) iter.next();
            String[] values = (String[]) requestParams.get(name);
            String valueStr = "";
            for(int i=0;i<values.length;i++){
                valueStr = (i == values.length-1)?valueStr+values[i]:valueStr+values[i]+",";
            }
            params.put(name,valueStr);
        }
        logger.info("支付宝参数，sign{}, trade_status:{},参数: {}",params.get("sign"),params.get("trade_status"),params.toString());
        //拿到回调，验证1。回调正确性，2。是不是支付宝发的 3。避免重复通知
        params.remove("sign_type");
        try{
            boolean alipayRSACheckedV2 = AlipaySignature.rsaCheckV2(params, Configs.getAlipayPublicKey(),"utf-8",Configs.getSignType());
            if(alipayRSACheckedV2){
                //业务逻辑
                return serverResponse.createByErrorMessage("Illegal request, Filed to process the request");
            }
        }catch (AlipayApiException e) {
            e.printStackTrace();
        }

        //todo  验证数据
        serverResponse serverResponse = iOrderService.aliCallback(params);
        if(serverResponse.isSuccess()){
            return Consts.AlipayCallback.RESPONSE_SUCCESS;
        }
        return Consts.AlipayCallback.RESPONSE_FAILED;
    }

    @RequestMapping("query_order_pay_status")
    @ResponseBody
    public serverResponse<Boolean> queryOrderPayStatus(HttpServletRequest request, Long orderNo){
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return serverResponse.createByErrorMessage("You need to login");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = jsonUtil.string2Obj(userJsonStr,User.class);
        if(user ==null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        serverResponse serverResponse = iOrderService.queryOrderPayStatus(user.getId(),orderNo);
        if(serverResponse.isSuccess()){
            return serverResponse.createBySuccess();
        }
        return serverResponse.createBySuccess(false);
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public serverResponse detail(HttpServletRequest request, Long orderNo){
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return serverResponse.createByErrorMessage("You need to login");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = jsonUtil.string2Obj(userJsonStr,User.class);
        if(user ==null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }

        return iOrderService.getOrderDetail(user.getId(),orderNo);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public serverResponse list(HttpServletRequest request, @RequestParam(value="pageNum", defaultValue ="1") int pageNum, @RequestParam(value="pageSize", defaultValue ="10")int pageSize){
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return serverResponse.createByErrorMessage("You need to login");
        }
        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
        User user = jsonUtil.string2Obj(userJsonStr,User.class);
        if(user ==null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        return iOrderService.getOrderList(user.getId(),pageNum,pageSize);
    }


}
