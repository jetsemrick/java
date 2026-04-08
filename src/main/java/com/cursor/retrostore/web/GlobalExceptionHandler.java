package com.cursor.retrostore.web;

import com.cursor.retrostore.catalog.CategoryNotFoundException;
import com.cursor.retrostore.catalog.ProductNotFoundException;
import com.cursor.retrostore.order.OrderNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler({ProductNotFoundException.class, CategoryNotFoundException.class, OrderNotFoundException.class})
    public ModelAndView handleNotFound(RuntimeException ex) {
        ModelAndView mv = new ModelAndView("error/404");
        mv.addObject("message", ex.getMessage());
        mv.setStatus(HttpStatus.NOT_FOUND);
        return mv;
    }
}
