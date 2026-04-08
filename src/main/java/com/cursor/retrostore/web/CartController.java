package com.cursor.retrostore.web;

import com.cursor.retrostore.cart.CartService;
import com.cursor.retrostore.order.CheckoutService;
import com.cursor.retrostore.order.Order;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class CartController {

    private final CartService cartService;
    private final CheckoutService checkoutService;

    public CartController(CartService cartService, CheckoutService checkoutService) {
        this.cartService = cartService;
        this.checkoutService = checkoutService;
    }

    @GetMapping("/cart")
    public String cart(Model model) {
        model.addAttribute("lines", cartService.getLines());
        model.addAttribute("subtotal", cartService.getSubtotal());
        model.addAttribute("itemCount", cartService.getTotalItemCount());
        return "cart";
    }

    @PostMapping("/cart/add")
    public String addToCart(
            @RequestParam long productId,
            @RequestParam(defaultValue = "1") int quantity,
            @RequestParam(required = false) String redirectTo) {
        cartService.addProduct(productId, quantity);
        if (redirectTo != null && !redirectTo.isBlank()) {
            return "redirect:" + redirectTo;
        }
        return "redirect:/products/" + productId;
    }

    @PostMapping("/cart/update")
    public String updateLine(@RequestParam long productId, @RequestParam int quantity) {
        cartService.setQuantity(productId, quantity);
        return "redirect:/cart";
    }

    @PostMapping("/cart/remove")
    public String removeLine(@RequestParam long productId) {
        cartService.removeLine(productId);
        return "redirect:/cart";
    }

    @GetMapping("/checkout")
    public String checkoutForm(Model model) {
        if (cartService.isEmpty()) {
            return "redirect:/cart";
        }
        if (!model.containsAttribute("checkoutForm")) {
            model.addAttribute("checkoutForm", new CheckoutForm());
        }
        model.addAttribute("lines", cartService.getLines());
        model.addAttribute("subtotal", cartService.getSubtotal());
        return "checkout";
    }

    @PostMapping("/checkout")
    public String checkoutSubmit(
            @Valid @ModelAttribute("checkoutForm") CheckoutForm checkoutForm,
            BindingResult bindingResult,
            Model model,
            RedirectAttributes redirectAttributes) {
        if (cartService.isEmpty()) {
            return "redirect:/cart";
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("lines", cartService.getLines());
            model.addAttribute("subtotal", cartService.getSubtotal());
            return "checkout";
        }
        String email = checkoutForm.getEmail() == null ? "" : checkoutForm.getEmail().trim();
        if (!email.isEmpty() && !email.contains("@")) {
            bindingResult.rejectValue("email", "email.invalid", "Enter a valid email or leave blank");
            model.addAttribute("lines", cartService.getLines());
            model.addAttribute("subtotal", cartService.getSubtotal());
            return "checkout";
        }
        Order order = checkoutService.placeOrder(cartService, email.isEmpty() ? null : email);
        redirectAttributes.addFlashAttribute("orderId", order.getId());
        return "redirect:/order/confirmation/" + order.getId();
    }

    @GetMapping("/order/confirmation/{id}")
    public String confirmation(@PathVariable long id, Model model) {
        // Order already persisted; reload for display
        Order order = checkoutService.findOrderForDisplay(id);
        model.addAttribute("order", order);
        return "order-confirmation";
    }
}
