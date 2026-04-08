package com.cursor.retrostore.order;

import com.cursor.retrostore.cart.CartLineView;
import com.cursor.retrostore.cart.CartService;
import com.cursor.retrostore.catalog.Product;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CheckoutServiceTest {

    @Mock
    private OrderRepository orderRepository;

    @InjectMocks
    private CheckoutService checkoutService;

    @Test
    void placeOrder_emptyCart_throws() {
        CartService cart = mock(CartService.class);
        when(cart.getLines()).thenReturn(List.of());

        assertThatThrownBy(() -> checkoutService.placeOrder(cart, null))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("empty");
    }

    @Test
    void placeOrder_persistsOrderAndClearsCart() {
        Product product = mock(Product.class);
        when(product.getId()).thenReturn(42L);
        when(product.getName()).thenReturn("Mock ISA Card");
        when(product.getPrice()).thenReturn(new BigDecimal("12.34"));

        CartService cart = mock(CartService.class);
        when(cart.getLines()).thenReturn(List.of(new CartLineView(product, 3)));

        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> invocation.getArgument(0));

        checkoutService.placeOrder(cart, "buyer@example.com");

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        Order saved = captor.getValue();
        assertThat(saved.getTotalAmount()).isEqualByComparingTo(new BigDecimal("37.02"));
        assertThat(saved.getLines()).hasSize(1);
        assertThat(saved.getLines().get(0).getProductName()).isEqualTo("Mock ISA Card");
        verify(cart).clear();
    }
}
