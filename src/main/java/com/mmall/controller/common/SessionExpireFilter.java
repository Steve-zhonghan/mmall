package com.mmall.controller.common;

import com.mmall.common.Consts;
import com.mmall.utility.CookieUtil;
import com.mmall.utility.RedisShardedPoolUtil;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

public class SessionExpireFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) servletRequest;
        String loginToken = CookieUtil.readLoginToken(httpServletRequest);
        if(StringUtils.isNotEmpty(loginToken)){
            //判断logintoken是否为空或者""
            //如果不为空，符合条件，继续拿user信息
            String user = RedisShardedPoolUtil.get(loginToken);
            if(user!=null){
                //如果user不为空，则重置session的时间，即调用expire命令
                RedisShardedPoolUtil.expire(loginToken, Consts.RedisCartCacheExTime.REDIS_SESSION_EX_TIME);
            }
        }
        filterChain.doFilter(servletRequest,servletResponse);
    }

    @Override
    public void destroy() {

    }
}
