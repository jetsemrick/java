package com.cursor.retrostore.catalog;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class CatalogServiceIntegrationTest {

    @Autowired
    private CatalogService catalogService;

    @Test
    void seedData_loadsCategoriesWithProducts() {
        List<Category> categories = catalogService.findAllCategoriesWithProducts();
        assertThat(categories).isNotEmpty();
        int productCount = categories.stream().mapToInt(c -> c.getProducts().size()).sum();
        assertThat(productCount).isGreaterThanOrEqualTo(8);
    }
}
