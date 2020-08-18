package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Consts;
import com.mmall.common.responseCode;
import com.mmall.common.serverResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import com.mmall.utility.CookieUtil;
import com.mmall.utility.RedisPoolUtil;
import com.mmall.utility.jsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping/")
public class shippingController {

    @Autowired
    private IShippingService iShippingService;

    //springMVC数据对象绑定 Shipping绑定
    @RequestMapping("add.do")
    @ResponseBody
    public serverResponse add(HttpServletRequest request, Shipping shipping){
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return serverResponse.createByErrorMessage("You need to login");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = jsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.add(user.getId(),shipping);
    }

    @RequestMapping("del.do")
    @ResponseBody
    public serverResponse del(HttpServletRequest request, Integer shippingId){
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return serverResponse.createByErrorMessage("You need to login");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = jsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.del(user.getId(),shippingId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public serverResponse update(HttpServletRequest request, Shipping shipping){
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return serverResponse.createByErrorMessage("You need to login");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = jsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.update(user.getId(),shipping);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public serverResponse<Shipping> select(HttpServletRequest request, Integer shippingId){
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return serverResponse.createByErrorMessage("You need to login");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = jsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.select(user.getId(),shippingId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public serverResponse<PageInfo> list(@RequestParam(value = "pageNum", defaultValue = "1")int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10")int pageSize,
                                         HttpServletRequest request){
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return serverResponse.createByErrorMessage("You need to login");
        }
        String userJsonStr = RedisPoolUtil.get(loginToken);
        User user = jsonUtil.string2Obj(userJsonStr,User.class);
        if(user == null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.list(user.getId(),pageNum,pageSize);
    }


}
