package com.cursor.retrostore.order;

import com.cursor.retrostore.cart.CartLineView;
import com.cursor.retrostore.cart.CartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Service
public class CheckoutService {

    private final OrderRepository orderRepository;

    public CheckoutService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    @Transactional(readOnly = true)
    public Order findOrderForDisplay(long id) {
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrderNotFoundException(id));
        order.getLines().size();
        return order;
    }

    @Transactional
    public Order placeOrder(CartService cart, String customerEmail) {
        List<CartLineView> lines = cart.getLines();
        if (lines.isEmpty()) {
            throw new IllegalStateException("Cart is empty");
        }
        BigDecimal total = lines.stream()
                .map(CartLineView::lineTotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        Order order = new Order(total, blankToNull(customerEmail));
        for (CartLineView line : lines) {
            order.addLine(new OrderLine(
                    line.product().getId(),
                    line.product().getName(),
                    line.product().getPrice(),
                    line.quantity()));
        }
        Order saved = orderRepository.save(order);
        cart.clear();
        return saved;
    }

    private static String blankToNull(String s) {
        if (s == null || s.isBlank()) {
            return null;
        }
        return s.trim();
    }
}
