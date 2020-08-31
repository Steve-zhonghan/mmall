package com.mmall.controller.backend;

import com.mmall.common.responseCode;
import com.mmall.common.serverResponse;
import com.mmall.pojo.User;
import com.mmall.service.ICategoryService;
import com.mmall.service.IUserService;
import com.mmall.utility.CookieUtil;
import com.mmall.utility.RedisShardedPoolUtil;
import com.mmall.utility.jsonUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/manage/category")
public class CategoryManageController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private ICategoryService iCategoryService;

    @RequestMapping("add_category.do")
    @ResponseBody
    public serverResponse addCategory(HttpServletRequest request, String categoryName, @RequestParam(value="parentId",defaultValue = "0") int parentId){
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
//        String loginToken = CookieUtil.readLoginToken(request);
//        if(StringUtils.isEmpty(loginToken)){
//            return serverResponse.createByErrorMessage("You need to login");
//        }
//        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
//        User user = jsonUtil.string2Obj(userJsonStr,User.class);
//        if(user==null){
//            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),"User need to log in");
//        }
//        //校验一下是否是管理员
//        if(iUserService.checkAdminRole(user).isSuccess()){
//            //是管理员
//            //增加处理分类的逻辑
//            return iCategoryService.addCategory(categoryName,parentId);
//        }else{
//            return serverResponse.createByErrorMessage("No privilege to operate");
//        }
        //全部通过拦截器验证是否登陆以及权限
        return iCategoryService.addCategory(categoryName,parentId);
    }

    @RequestMapping("set_category_name.do")
    @ResponseBody
    public serverResponse setCategoryName(HttpServletRequest request,Integer categoryId,String categoryName){
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
//        String loginToken = CookieUtil.readLoginToken(request);
//        if(StringUtils.isEmpty(loginToken)){
//            return serverResponse.createByErrorMessage("You need to login");
//        }
//        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
//        User user = jsonUtil.string2Obj(userJsonStr,User.class);
//        if(user==null){
//            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),"User need to log in");
//        }
//        //校验一下是否是管理员
//        if(iUserService.checkAdminRole(user).isSuccess()){
//            //更新category name
//            return iCategoryService.updateCategoryName(categoryId,categoryName);
//        }else{
//            return serverResponse.createByErrorMessage("No privilege to operate");
//        }
        //全部通过拦截器验证是否登陆以及权限
        return iCategoryService.updateCategoryName(categoryId,categoryName);
    }

    @RequestMapping("get_category.do")
    @ResponseBody
    //无递归
    public serverResponse getChildrenParallelCategory(HttpServletRequest request, @RequestParam(value = "categoryId",defaultValue ="0")Integer categoryId){
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
//        String loginToken = CookieUtil.readLoginToken(request);
//        if(StringUtils.isEmpty(loginToken)){
//            return serverResponse.createByErrorMessage("You need to login");
//        }
//        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
//        User user = jsonUtil.string2Obj(userJsonStr,User.class);
//        if(user==null){
//            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),"User need to log in");
//        }
//        if(iUserService.checkAdminRole(user).isSuccess()){
//            //查询category name
//            return iCategoryService.getChildrenParallelCategory(categoryId);
//        }else{
//            return serverResponse.createByErrorMessage("No privilege to operate");
//        }
        //全部通过拦截器验证是否登陆以及权限
        return iCategoryService.getChildrenParallelCategory(categoryId);
    }

    @RequestMapping("get_deep_category.do")
    @ResponseBody
    //无递归
    public serverResponse getCategoryAndDeepChildrenCategory(HttpServletRequest request, @RequestParam(value = "categoryId",defaultValue ="0")Integer categoryId){
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
//        String loginToken = CookieUtil.readLoginToken(request);
//        if(StringUtils.isEmpty(loginToken)){
//            return serverResponse.createByErrorMessage("You need to login");
//        }
//        String userJsonStr = RedisShardedPoolUtil.get(loginToken);
//        User user = jsonUtil.string2Obj(userJsonStr,User.class);
//        if(user==null){
//            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),"User need to log in");
//        }
//        if(iUserService.checkAdminRole(user).isSuccess()){
//            //查询当前节点id和递归子节点id
//            return iCategoryService.selectCategoryAndChildrenById(categoryId);
//        }else{
//            return serverResponse.createByErrorMessage("No privilege to operate");
//        }
        //全部通过拦截器验证是否登陆以及权限
        return iCategoryService.selectCategoryAndChildrenById(categoryId);
    }

}
