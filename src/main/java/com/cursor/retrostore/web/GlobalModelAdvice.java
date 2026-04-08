package com.cursor.retrostore.web;

import com.cursor.retrostore.cart.CartService;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalModelAdvice {

    private final CartService cartService;

    public GlobalModelAdvice(CartService cartService) {
        this.cartService = cartService;
    }

    @ModelAttribute("cartItemCount")
    public int cartItemCount() {
        return cartService.getTotalItemCount();
    }
}
