package com.mmall.controller.common.Interceptor;

import com.google.common.collect.Maps;
import com.mmall.common.Consts;
import com.mmall.common.RedisShardedPool;
import com.mmall.common.serverResponse;
import com.mmall.pojo.User;
import com.mmall.utility.CookieUtil;
import com.mmall.utility.RedisShardedPoolUtil;
import com.mmall.utility.jsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.data.Json;
import org.apache.commons.lang.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

@Slf4j
public class AuthorityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object handler) throws Exception {
        log.info("preHandle");
        //请求中Controller中的方法名
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        //解析HandlerMethod
        String methodName = handlerMethod.getMethod().getName();
        String className = handlerMethod.getBean().getClass().getSimpleName();

        //解析参数，具体的参数key以及value是什么，打印日志
        StringBuffer requestParamBuffer = new StringBuffer();
        Map paramMap = httpServletRequest.getParameterMap();
        Iterator it = paramMap.entrySet().iterator();
        while(it.hasNext()){
            Map.Entry entry = (Map.Entry) it.next();
            String mapKey = (String)entry.getKey();
            String mapValue = StringUtils.EMPTY;
            //request这个参数的map，立马进的value返回的是一个String[]
            Object obj = entry.getValue();
            if(obj instanceof String[]){
                String[] strs = (String[]) obj;
                mapValue = Arrays.toString(strs);
            }
            requestParamBuffer.append(mapKey).append("=").append(mapValue);
        }
        log.info("拦截器拦截到请求, className:{},methodName:{},param:{}",className,methodName,requestParamBuffer.toString());
        User user = null;
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isNotEmpty(loginToken)){
            String userJsonStr = RedisShardedPoolUtil.get(loginToken);
            user = jsonUtil.string2Obj(userJsonStr,User.class);
        }
        if(user==null || (user.getRole()!= Consts.Role.ROLE_ADMIN)){
            //返回false，即不会调用controller里的方法
            //这里要返回一个serverResponse，而不是boolean值
            httpServletResponse.reset();//必须要reset，否则爆异常，getwriter（） has already been called for this response
            //要想重写输出，必reset
            httpServletResponse.setCharacterEncoding("UTF-8");//这里要设置编码，否则乱码
            httpServletResponse.setContentType("application/json;charset=UTF-8");//设置返回值类型
            PrintWriter out = httpServletResponse.getWriter();
            if(user==null){
                //针对richtext_img_upload富文本上传的问题进行解决
                if(StringUtils.equals(className,"productManageController")&&StringUtils.equals(methodName,"richtext_img_upload")){
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","Please login as an admin");
                    out.print(jsonUtil.obj2String(resultMap));
                }else {
                    out.print(jsonUtil.obj2String(serverResponse.createByErrorMessage("Intercepted by interceptor for not logging in")));
                }
            }else{
                if(StringUtils.equals(className,"productManageController")&&StringUtils.equals(methodName,"richtext_img_upload")){
                    Map resultMap = Maps.newHashMap();
                    resultMap.put("success",false);
                    resultMap.put("msg","No privilege to operate");
                    out.print(jsonUtil.obj2String(resultMap));
                }else {
                    out.print(jsonUtil.obj2String(serverResponse.createByErrorMessage("Intercepted by interceptor for not logging in")));
                }
            }
            out.flush();
            out.close();
            return false;//不许进入controller
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, ModelAndView modelAndView) throws Exception {
        log.info("postHandel");
    }

    @Override
    public void afterCompletion(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) throws Exception {
        log.info("afterCompletion");
    }
}
