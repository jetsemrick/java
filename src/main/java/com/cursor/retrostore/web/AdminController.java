package com.cursor.retrostore.web;

import com.cursor.retrostore.catalog.CatalogService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final CatalogService catalogService;

    public AdminController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/admin/catalog")
    public String catalog(Model model) {
        model.addAttribute("categories", catalogService.findAllCategoriesWithProducts());
        return "admin/catalog";
    }
}
