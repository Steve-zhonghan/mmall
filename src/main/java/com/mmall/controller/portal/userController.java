package com.mmall.controller.portal;

import com.mmall.common.Consts;
import com.mmall.common.responseCode;
import com.mmall.common.serverResponse;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.net.ssl.HttpsURLConnection;
import javax.servlet.http.HttpSession;

@Controller
@RequestMapping("/user/")
public class userController {

    @Autowired
    private IUserService iUserService;
    /**
     * The function for user to login in
     * @param username
     * @param password
     * @param session
     * @return
     */
    @RequestMapping(value="login.do",method= RequestMethod.POST)
    @ResponseBody
    public serverResponse<User> login(String username, String password, HttpSession session){
        serverResponse<User> response = iUserService.login(username,password);
        if(response.isSuccess()){
            session.setAttribute(Consts.CURRENT_USER,response.getData());
        }
        return response;
    }

    @RequestMapping(value="logout.do",method= RequestMethod.POST)
    @ResponseBody
    public serverResponse<User> logout(HttpSession session){
        session.removeAttribute(Consts.CURRENT_USER);
        return serverResponse.createBySuccess();
    }

    @RequestMapping(value="register.do",method= RequestMethod.POST)
    @ResponseBody
    //springMVC数据绑定
    public serverResponse<String> register(User user){
        return iUserService.register(user);
    }

    @RequestMapping(value="checkValid.do",method= RequestMethod.POST)
    @ResponseBody
    public serverResponse<String> checkValid(String str,String type){
        return iUserService.checkValid(str,type);
    }

    @RequestMapping(value="get_user_info.do",method= RequestMethod.POST)
    @ResponseBody
    public serverResponse<User> getUserInfo(HttpSession session){
        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        if(user!=null){
            return serverResponse.createBySuccess(user);
        }
        return serverResponse.createByErrorMessage("Failed to get current user information");
    }

    @RequestMapping(value="forget_get_question.do",method= RequestMethod.POST)
    @ResponseBody
    public serverResponse<String> forgetGetQuestion(String username){
        return iUserService.selectQuestion(username);
    }

    @RequestMapping(value="forget_check_answer.do",method= RequestMethod.POST)
    @ResponseBody
    //token,本地guava缓存做token
    public serverResponse<String> forgetCheckAnswer(String username,String question,String answer){
        return iUserService.checkAnswer(username,question,answer);
    }

    @RequestMapping(value="forget_reset_password.do",method= RequestMethod.POST)
    @ResponseBody
    public serverResponse<String> forgetResetPassword(String username,String new_password,String forgetToken){
        return iUserService.forgetResetPassword(username,new_password,forgetToken);
    }

    @RequestMapping(value="reset_password.do",method= RequestMethod.POST)
    @ResponseBody
    public serverResponse<String> resetPassword(HttpSession session,String old_password,String new_password){
        User user = (User)session.getAttribute(Consts.CURRENT_USER);
        if(user==null){
            return serverResponse.createByErrorMessage("The user does not signed in");
        }
        return iUserService.resetPassword(old_password,new_password,user);
    }

    @RequestMapping(value="update_information.do",method= RequestMethod.POST)
    @ResponseBody
    public serverResponse<User> update_information(HttpSession session,User user){
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        if(currentUser==null){
            return serverResponse.createByErrorMessage("The user does not signed in");
        }
        user.setId(currentUser.getId());
        user.setUsername(currentUser.getUsername());
        serverResponse response = iUserService.updateInformation(user);
        if(response.isSuccess()){
            session.setAttribute(Consts.CURRENT_USER,response.getData());
        }
        return response;
    }

    @RequestMapping(value="get_information.do",method= RequestMethod.POST)
    @ResponseBody
    public serverResponse<User> get_information(HttpSession session){
        User currentUser = (User)session.getAttribute(Consts.CURRENT_USER);
        if(currentUser==null){
            return serverResponse.createByErrorCodeMessage(responseCode.NEED_LOGIN.getCode(),"You need to login in，code=10");
        }
        return iUserService.getInformation(currentUser.getId());
    }
}
