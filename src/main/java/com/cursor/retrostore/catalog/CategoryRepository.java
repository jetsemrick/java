package com.cursor.retrostore.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    List<Category> findAllByOrderBySortOrderAsc();

    @Query("SELECT DISTINCT c FROM Category c LEFT JOIN FETCH c.products p ORDER BY c.sortOrder ASC, p.name ASC")
    List<Category> findAllWithProductsOrdered();
}
