package com.cursor.retrostore.catalog;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    List<Product> findByCategoryIdOrderByNameAsc(Long categoryId);

    Optional<Product> findBySku(String sku);
}
