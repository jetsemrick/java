package com.cursor.retrostore.catalog;

public class CategoryNotFoundException extends RuntimeException {

    public CategoryNotFoundException(Long id) {
        super("Category not found: " + id);
    }
}
