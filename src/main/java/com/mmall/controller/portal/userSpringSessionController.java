package com.mmall.controller.portal;

import com.mmall.common.Consts;
import com.mmall.common.serverResponse;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.utility.CookieUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/springsession")
public class userSpringSessionController {

    @Autowired
    private IUserService iUserService;

    @Autowired
    private UserMapper userMapper;
    /**
     * The function for user to login
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value="login.do",method= RequestMethod.POST)
    @ResponseBody
    public serverResponse<User> login(String username, String password, HttpSession session, HttpServletResponse httpServletResponse){
        serverResponse<User> response = iUserService.login(username,password);
        if(response.isSuccess()){
              session.setAttribute(Consts.CURRENT_USER,response.getData());
//            CookieUtil.writeLoginToken(httpServletResponse,session.getId());
//            RedisShardedPoolUntil.setEx(session.getId(), jsonUtil.obj2String(response.getData()), Consts.RedisCartCacheExTime.REDIS_SESSION_EXTIME);
        }
        return response;
    }

    @RequestMapping(value="logout.do",method= RequestMethod.POST)
    @ResponseBody
    public serverResponse<User> logout(HttpSession session,HttpServletRequest request,HttpServletResponse response){
        String loginToken = CookieUtil.readLoginToken(request);
        if(StringUtils.isEmpty(loginToken)){
            return serverResponse.createByErrorMessage("You need to login");
        }
//        CookieUtil.delLoginToken(request,response);
//        RedisShardedPoolUntil.del(loginToken);
        session.removeAttribute(Consts.CURRENT_USER);
        return serverResponse.createBySuccess();
    }

    @RequestMapping(value="get_user_info.do",method= RequestMethod.POST)
    @ResponseBody
    public serverResponse<User> getUserInfo(HttpServletRequest request,HttpSession session){
//        User user = (User)session.getAttribute(Consts.CURRENT_USER);
//        String loginToken = CookieUtil.readLoginToken(request);
//        if(StringUtils.isEmpty(loginToken)){
//            return serverResponse.createByErrorMessage("You need to login");
//        }
//        String userJsonStr = RedisShardedPoolUntil.get(loginToken);
//        User user = jsonUtil.string2Obj(userJsonStr,User.class);
        User user = (User)session.getAttribute(Consts.CURRENT_USER);

        if(user!=null){
            return serverResponse.createBySuccess(user);
        }
        return serverResponse.createByErrorMessage("Failed to get current user information");
    }
}
