package com.cursor.retrostore.web;

import com.cursor.retrostore.catalog.CatalogService;
import com.cursor.retrostore.catalog.Category;
import com.cursor.retrostore.catalog.Product;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@Controller
public class StoreController {

    private final CatalogService catalogService;

    public StoreController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/")
    public String home(Model model) {
        List<Category> categories = catalogService.findAllCategoriesWithProducts();
        model.addAttribute("categories", categories);
        return "home";
    }

    @GetMapping("/products/{id}")
    public String product(@PathVariable long id, Model model) {
        Product product = catalogService.getProduct(id);
        model.addAttribute("product", product);
        return "product";
    }

    @GetMapping("/categories/{id}")
    public String category(@PathVariable long id, Model model) {
        model.addAttribute("category", catalogService.getCategory(id));
        List<com.cursor.retrostore.catalog.Product> products = catalogService.findProductsByCategory(id);
        model.addAttribute("products", products);
        return "category";
    }
}
