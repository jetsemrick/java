package com.cursor.retrostore.cart;

import com.cursor.retrostore.catalog.Product;

import java.math.BigDecimal;

public record CartLineView(Product product, int quantity) {

    public BigDecimal lineTotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}
