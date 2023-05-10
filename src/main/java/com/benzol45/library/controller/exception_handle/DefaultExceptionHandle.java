package com.benzol45.library.controller.exception_handle;

import com.benzol45.library.exception.IncorrectDataFromClientException;
import com.benzol45.library.exception.ObjectNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class DefaultExceptionHandle {
    //https://spring.io/blog/2013/11/01/exception-handling-in-spring-mvc

    @ExceptionHandler(value = Exception.class)
    public ModelAndView defaultErrorHandler(HttpServletRequest req, HttpServletResponse res, Exception e) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("url", req.getRequestURL());
        mav.addObject("exceptionClass", e.getClass().getName());
        mav.addObject("exceptionMessage", e.getMessage());
        mav.setViewName("error/AnyException");

        if (e.getClass()== ObjectNotFoundException.class) {
            res.setStatus(HttpStatus.NOT_FOUND.value());
        }
        else if (e.getClass()== IncorrectDataFromClientException.class) {
            res.setStatus(HttpStatus.BAD_REQUEST.value());
        } else  {
            res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        return mav;
    }
}
