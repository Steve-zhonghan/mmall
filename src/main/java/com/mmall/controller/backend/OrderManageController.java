package com.mmall.controller.backend;

import com.github.pagehelper.PageInfo;
import com.mmall.common.Consts;
import com.mmall.common.responseCode;
import com.mmall.common.serverResponse;
import com.mmall.pojo.User;
import com.mmall.service.IOrderService;
import com.mmall.service.IUserService;
import com.mmall.vo.OrderVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/order")
public class OrderManageController {
    
    @Autowired
    private IUserService iUserService;

    @Autowired
    private IOrderService iOrderService;

    @RequestMapping("list.do")
    @ResponseBody
    public serverResponse<PageInfo> orderList(HttpSession session,@RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                              @RequestParam(value = "pageSize",defaultValue = "10")int pageSize){
        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        if(user==null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),"The user need to login");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.manageList(pageNum,pageSize);
        }else{
            return serverResponse.createByErrorMessage("no privilege to sign in");
        }
    }

    @RequestMapping("detail.do")
    @ResponseBody
    public serverResponse<OrderVo> orderDetail(HttpSession session, Long orderNo){
        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        if(user==null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),"The user need to login");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.manageDetail(orderNo);
        }else{
            return serverResponse.createByErrorMessage("no privilege to sign in");
        }
    }

    @RequestMapping("search.do")
    @ResponseBody
    public serverResponse<PageInfo> orderSearch(HttpSession session, Long orderNo,
                                               @RequestParam(value = "pageNum",defaultValue = "1")int pageNum,
                                               @RequestParam(value = "pageSize",defaultValue = "10")int pageSiz){
        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        if(user==null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),"The user need to login");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.manageSearch(orderNo,pageNum,pageSiz);
        }else{
            return serverResponse.createByErrorMessage("no privilege to sign in");
        }
    }

    @RequestMapping("send_goods.do")
    @ResponseBody
    public serverResponse<String> orderSendGoods(HttpSession session, Long orderNo){
        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        if(user==null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),"The user need to login");
        }
        if(iUserService.checkAdminRole(user).isSuccess()){
            return iOrderService.manageSendGoods(orderNo);
        }else{
            return serverResponse.createByErrorMessage("no privilege to sign in");
        }
    }
}
