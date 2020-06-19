package com.mmall.service;

import com.mmall.common.serverResponse;
import com.mmall.pojo.User;

public interface IUserService {

    serverResponse<User> login(String username, String password);
    serverResponse<String> register(User user);
    serverResponse<String> checkValid(String str,String type);
    serverResponse selectQuestion(String username);
    serverResponse<String> checkAnswer(String username,String question, String answer);
    serverResponse<String> forgetResetPassword(String username,String new_password,String forgetToken);
    serverResponse<String> resetPassword(String old_password,String new_password,User user);
    serverResponse<User> updateInformation(User user);
    serverResponse<User> getInformation(Integer userId);
    serverResponse checkAdminRole(User user);
}
