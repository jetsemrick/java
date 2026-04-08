package com.cursor.retrostore.order;

public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(long id) {
        super("Order not found: " + id);
    }
}
