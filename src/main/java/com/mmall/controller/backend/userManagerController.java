package com.mmall.controller.backend;

import com.mmall.common.Consts;
import com.mmall.common.serverResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.servlet.DispatcherServlet;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.HttpServletBean;
import org.springframework.web.servlet.handler.SimpleUrlHandlerMapping;

import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/manage/user")
public class userManagerController {

    @Autowired
    private IUserService iUserService;

    @RequestMapping(value = "login.do",method = RequestMethod.POST)
    @ResponseBody
    public serverResponse<User> login(String username, String password, HttpSession session){
        serverResponse<User> response = iUserService.login(username,password);
        if(response.isSuccess()){
            User user = response.getData();
            if(user.getRole() == Consts.Role.ROLE_ADMIN){
                //administrator
                session.setAttribute(Consts.CURRENT_USER,user);
                return response;
            }else{
                return serverResponse.createByErrorMessage(" no privilege to sign in");
            }
        }
        return response;
    }
}
