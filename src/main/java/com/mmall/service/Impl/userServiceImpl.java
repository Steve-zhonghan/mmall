package com.mmall.service.Impl;

import com.mmall.common.Consts;
import com.mmall.common.serverResponse;
import com.mmall.common.tokenCache;
import com.mmall.dao.UserMapper;
import com.mmall.pojo.User;
import com.mmall.service.IUserService;
import com.mmall.utility.MD5Util;
import org.apache.commons.codec.digest.Md5Crypt;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service("iUserService")
public class userServiceImpl implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public serverResponse<User> login(String username, String password) {
       int resultCount = userMapper.checkUsername(username);
       if(resultCount == 0 ){
           return serverResponse.createByErrorMessage("The user does not exist");
       }

       //todo password is processed by MD5. DB存储用MD5加密
        String md5Password = MD5Util.MD5EncodeUtf8(password);
        User user  = userMapper.selectLogin(username,md5Password);
       if(user==null){
           return serverResponse.createByErrorMessage("Wrong password");
       }
       user.setPassword(StringUtils.EMPTY);
       return serverResponse.createBySuccess("Login successfully",user);
    }

    @Override
    public serverResponse<String> register(User user){
        serverResponse validResponse = this.checkValid(user.getUsername(),Consts.USERNAME);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        validResponse = this.checkValid(user.getEmail(),Consts.EMAIL);
        if(!validResponse.isSuccess()){
            return validResponse;
        }
        user.setRole(Consts.Role.ROLE_CUSTOMER);
        //MD5加密，MD5工具
        user.setPassword(MD5Util.MD5EncodeUtf8(user.getPassword()));
        int resultCount = userMapper.insert(user);
        if(resultCount == 0){
            return serverResponse.createByErrorMessage("Failed to sign up user");
        }
        return serverResponse.createBySuccess("Succeed to sign up new user");
    }

    @Override
    public serverResponse<String> checkValid(String str,String type){
        if(StringUtils.isNotBlank(type)){
            //开始校验
            if(Consts.USERNAME.equals(type)){
                int resultCount = userMapper.checkUsername(str);
                if(resultCount>0){
                    return serverResponse.createByErrorMessage("Existed user");
                }
            }
            if(Consts.EMAIL.equals(type)){
                int resultCount = userMapper.checkEmail(str);
                if(resultCount>0){
                    return serverResponse.createByErrorMessage("Existed email");
                }
            }
        }else{
            return serverResponse.createByErrorMessage("Wrong parameter");
        }
        return serverResponse.createBySuccessMessage("Succeed");
    }

    @Override
    public serverResponse selectQuestion(String username){
        serverResponse validResponse = this.checkValid(username,Consts.USERNAME);
        if(validResponse.isSuccess()){
            return serverResponse.createByErrorMessage("Non-existed user");
        }
        String question = userMapper.selectQuestionByUsername(username);
        if(StringUtils.isNotBlank(question)){
            return serverResponse.createBySuccess(question);
        }
        return serverResponse.createByErrorMessage("Blank question");
    }

    @Override
    public serverResponse<String> checkAnswer(String username,String question, String answer){
        int resultCount = userMapper.checkAnswer(username,question,answer);
        if(resultCount>0){
            //说明答案正确，生成token
            String forgetToken = UUID.randomUUID().toString();
            tokenCache.setKey(tokenCache.TOKEN_PREFIX+username,forgetToken);
            return serverResponse.createBySuccess(forgetToken);
        }
        return serverResponse.createByErrorMessage("Wrong answer");
    }

    @Override
    public serverResponse<String> forgetResetPassword(String username,String new_password,String forgetToken){
        if(StringUtils.isBlank(forgetToken)){
            return serverResponse.createByErrorMessage("wrong parameter,token is needed");
        }
        serverResponse validResponse = this.checkValid(username,Consts.USERNAME);
        if(validResponse.isSuccess()){
            return serverResponse.createByErrorMessage("Non-existed user");
        }
        String token  = tokenCache.getKey(tokenCache.TOKEN_PREFIX+username);
        if(StringUtils.isBlank(token)){
            return serverResponse.createByErrorMessage("token is not valid or expired");
        }
        //这个equals方法考虑null的情况，比string的equals方法安全
        if(StringUtils.equals(forgetToken,token)){
            String md5Password = MD5Util.MD5EncodeUtf8(new_password);
            int rowCount = userMapper.updatePasswordByUserName(username, md5Password);
            if(rowCount>0){
                return serverResponse.createBySuccessMessage("password set successfully");
            }
        }else{
            return serverResponse.createByErrorMessage("an mistake on token, please reacquire token for resetting password");
        }
        return serverResponse.createByErrorMessage("Failed to reset password");
    }

    @Override
    public serverResponse<String> resetPassword(String old_password,String new_password,User user){
        //防止横向越权，校验用户旧密码，一定要指定是这个用户，因为会查询一个count（1），如果不指定ID，那么结果就是true；
        int resultCount = userMapper.checkPassword(MD5Util.MD5EncodeUtf8(old_password),user.getId());
        if(resultCount==0){
            return serverResponse.createByErrorMessage("wrong old password");
        }
        user.setPassword(MD5Util.MD5EncodeUtf8(new_password));
        int updateCount = userMapper.updateByPrimaryKeySelective(user);
        if(updateCount>0){
            return serverResponse.createBySuccessMessage("password reset successfully");
        }
        return serverResponse.createByErrorMessage("failed to reset password");
    }

    @Override
    public serverResponse<User> updateInformation(User user){
        //username不能被更新
        //email也需要校验，是否唯一
        int resultCount = userMapper.checkEmailByUserId(user.getEmail(),user.getId());
        if(resultCount>0){
            return serverResponse.createByErrorMessage("The email is already existed, please try another email address");
        }
        User updateUser = new User();
        updateUser.setId(user.getId());
        updateUser.setEmail(user.getEmail());
        updateUser.setPhone(user.getPhone());
        updateUser.setQuestion(user.getQuestion());
        updateUser.setAnswer(user.getAnswer());

        int updateCount = userMapper.updateByPrimaryKeySelective(updateUser);
        if(updateCount>0){
            return serverResponse.createBySuccess("Succeed to update user information",user);
        }
        return serverResponse.createByErrorMessage("Failed to update user information");
    }

    @Override
    public serverResponse<User> getInformation(Integer userId){
        User user = userMapper.selectByPrimaryKey(userId);
        if(user == null){
            return serverResponse.createByErrorMessage("Can't find current user");
        }
        user.setPassword(StringUtils.EMPTY);
        return serverResponse.createBySuccess(user);
    }

    //backend

    @Override
    public serverResponse checkAdminRole(User user){
        if(user!=null&&user.getRole().intValue() == Consts.Role.ROLE_ADMIN){
            return serverResponse.createBySuccess();
        }else{
            return serverResponse.createByError();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED , readOnly = false)
    @Override
    public void transaction(){
        User user1 = new User();
        user1.setId(40);
        user1.setUsername("zhanghan2");
        user1.setPassword("111");
        user1.setAnswer("daan");
        user1.setEmail("zhang@18.com");
        user1.setPhone("11111");
        user1.setQuestion("question");
        user1.setRole(1);
        int result = userMapper.insert(user1);

        //User user  = userMapper.selectLogin("steve","D69403E2673E611D4CBD3FAD6FD1788E");
        //System.out.println(user.getUsername());
        throw new RuntimeException();

    }
}
