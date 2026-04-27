package com.andromedaastroshop.crudfullstack.crud_fullstack.categories.service;

import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.dto.CategoryResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.dto.CreateCategoryRequest;

import java.util.List;

public interface CategoryService {
    CategoryResponse create (CreateCategoryRequest request);

    CategoryResponse findById(Long id);

    CategoryResponse findBySlug(String slug);

    List<CategoryResponse> findAllCategories();

    CategoryResponse updateById(Long id, CreateCategoryRequest request);

    String deleteById(Long id);
}
