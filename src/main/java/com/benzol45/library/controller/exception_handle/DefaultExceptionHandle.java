package com.benzol45.library.controller.exception_handle;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class DefaultExceptionHandle {
    //https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, Exception e) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("url", req.getRequestURL());
        mav.addObject("exceptionClass", e.getClass().getName());
        mav.addObject("exceptionMessage", e.getMessage());
        mav.setViewName("/error/AnyException");
        return mav;
    }
}
