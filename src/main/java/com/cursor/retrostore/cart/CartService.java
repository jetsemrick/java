package com.cursor.retrostore.cart;

import com.cursor.retrostore.catalog.CatalogService;
import com.cursor.retrostore.catalog.Product;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Component
@Scope(value = "session", proxyMode = ScopedProxyMode.TARGET_CLASS)
public class CartService {

    private final CatalogService catalogService;
    private final Map<Long, Integer> quantitiesByProductId = new LinkedHashMap<>();

    public CartService(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    public void addProduct(long productId, int quantity) {
        if (quantity <= 0) {
            quantitiesByProductId.remove(productId);
            return;
        }
        Product p = catalogService.getProduct(productId);
        int next = quantitiesByProductId.getOrDefault(productId, 0) + quantity;
        if (next > p.getStockQuantity()) {
            next = p.getStockQuantity();
        }
        if (next <= 0) {
            quantitiesByProductId.remove(productId);
        } else {
            quantitiesByProductId.put(productId, next);
        }
    }

    public void setQuantity(long productId, int quantity) {
        if (quantity <= 0) {
            quantitiesByProductId.remove(productId);
            return;
        }
        Product p = catalogService.getProduct(productId);
        int q = Math.min(quantity, p.getStockQuantity());
        quantitiesByProductId.put(productId, q);
    }

    public void removeLine(long productId) {
        quantitiesByProductId.remove(productId);
    }

    public void clear() {
        quantitiesByProductId.clear();
    }

    public boolean isEmpty() {
        return quantitiesByProductId.isEmpty();
    }

    public List<CartLineView> getLines() {
        return quantitiesByProductId.entrySet().stream()
                .map(e -> new CartLineView(catalogService.getProduct(e.getKey()), e.getValue()))
                .collect(Collectors.toList());
    }

    public BigDecimal getSubtotal() {
        return getLines().stream()
                .map(CartLineView::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalItemCount() {
        return quantitiesByProductId.values().stream().mapToInt(Integer::intValue).sum();
    }
}
