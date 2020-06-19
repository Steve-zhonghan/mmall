package com.mmall.controller.portal;

import com.mmall.common.Consts;
import com.mmall.common.responseCode;
import com.mmall.common.serverResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/cart/")
public class cartController {

    @Autowired
    private ICartService iCartService;

    @RequestMapping("list.do")
    @ResponseBody
    public serverResponse list(HttpSession session, Integer count,Integer productId){
        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        if(user == null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.list(user.getId());
    }

    @RequestMapping("add.do")
    @ResponseBody
    public serverResponse add(HttpSession session, Integer count,Integer productId){
        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        if(user == null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.add(user.getId(),productId,count);
    }

    @RequestMapping("update.do")
    @ResponseBody
    public serverResponse update(HttpSession session, Integer count,Integer productId){
        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        if(user == null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.update(user.getId(),count,productId);
    }

    @RequestMapping("delete_product.do")
    @ResponseBody
    public serverResponse delete_product(HttpSession session,String productIds){
        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        if(user == null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),responseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.deleteProduct(user.getId(),productIds);
    }

    //全选
    @RequestMapping("select_all.do")
    @ResponseBody
    public serverResponse selectAll(HttpSession session) {
        User user = (User) session.getAttribute(Consts.CURRENT_USER);
        if (user == null) {
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(), responseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(),null, Consts.Cart.CHECKED);
    }

    //全反选
    @RequestMapping("un_select_all.do")
    @ResponseBody
    public serverResponse unSelectAll(HttpSession session) {
        User user = (User) session.getAttribute(Consts.CURRENT_USER);
        if (user == null) {
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(), responseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(), null, Consts.Cart.UN_CHECKED);
    }

    //单独选
    @RequestMapping("un_select.do")
    @ResponseBody
    public serverResponse unSelectAll(HttpSession session,Integer productId) {
        User user = (User) session.getAttribute(Consts.CURRENT_USER);
        if (user == null) {
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(), responseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(), productId, Consts.Cart.UN_CHECKED);
    }

    //单独反选
    @RequestMapping("select.do")
    @ResponseBody
    public serverResponse select(HttpSession session,Integer productId) {
        User user = (User) session.getAttribute(Consts.CURRENT_USER);
        if (user == null) {
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(), responseCode.NEED_LOGIN.getDesc());
        }
        return iCartService.selectOrUnSelect(user.getId(), productId, Consts.Cart.CHECKED);
    }

    //查询当前用户的购物处里面的产品数量
    @RequestMapping("get_cart_product_count.do")
    @ResponseBody
    public serverResponse<Integer> getCartProductCount(HttpSession session) {
        User user = (User) session.getAttribute(Consts.CURRENT_USER);
        if (user == null) {
            return serverResponse.createBySuccess(0);
        }
        return iCartService.getCartProductCount(user.getId());
    }
}
