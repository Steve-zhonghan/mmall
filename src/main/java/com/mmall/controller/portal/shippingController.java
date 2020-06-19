package com.mmall.controller.portal;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Consts;
import com.mmall.common.responseCode;
import com.mmall.common.serverResponse;
import com.mmall.pojo.Shipping;
import com.mmall.pojo.User;
import com.mmall.service.IShippingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/shipping/")
public class shippingController {

    @Autowired
    private IShippingService iShippingService;

    //springMVC数据对象绑定 Shipping绑定
    @RequestMapping("add.do")
    @ResponseBody
    public serverResponse add(HttpSession session, Shipping shipping){
        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        if(user == null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.add(user.getId(),shipping);
    }

    @RequestMapping("del.do")
    @ResponseBody
    public serverResponse del(HttpSession session, Integer shippingId){
        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        if(user == null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.del(user.getId(),shippingId);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public serverResponse update(HttpSession session, Shipping shipping){
        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        if(user == null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.update(user.getId(),shipping);
    }

    @RequestMapping("select.do")
    @ResponseBody
    public serverResponse<Shipping> select(HttpSession session, Integer shippingId){
        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        if(user == null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.select(user.getId(),shippingId);
    }

    @RequestMapping("list.do")
    @ResponseBody
    public serverResponse<PageInfo> list(@RequestParam(value = "pageNum", defaultValue = "1")int pageNum,
                                         @RequestParam(value = "pageSize", defaultValue = "10")int pageSize,
                                         HttpSession session){
        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        if(user == null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        return iShippingService.list(user.getId(),pageNum,pageSize);
    }


}
