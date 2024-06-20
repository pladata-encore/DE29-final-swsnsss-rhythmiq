package com.example.mrs_spring_web.Exception;

import java.io.IOException;
import java.net.URLEncoder;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class HubExceptionHandler {
    
    @ExceptionHandler(value = Exception.class)
    public void ExceptionHandler(Exception e, HttpServletResponse response) throws IOException{
        ExceptionTypes exceptionTypes = ExceptionTypes.valueOf(e.getClass().getSimpleName());
        String errorMessage = exceptionTypes.getMsg(); 
        log.error("message: " + errorMessage);
        errorMessage = URLEncoder.encode(errorMessage, "UTF-8");
        response.sendRedirect("/user/main?error=true&exception="+errorMessage);
    }
}

