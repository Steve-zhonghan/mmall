package com.mmall.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.json.MappingJacksonJsonView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class ExceptionResolver implements HandlerExceptionResolver {

    @Override
    public ModelAndView resolveException(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, Object o, Exception e) {
        log.error("{} Exception",httpServletRequest.getRequestURI(),e);
        ModelAndView modelAndView = new ModelAndView(new MappingJacksonJsonView());

        //当使用jackson2.x 的时候使用Mappingjackson2andView，课程中使用的是1.9
        modelAndView.addObject("status",responseCode.ERROR.getCode());
        modelAndView.addObject("msg","Exception happened");
        modelAndView.addObject("data","e.toString()");
        return modelAndView;
    }
}
