package com.andromedaastroshop.crudfullstack.crud_fullstack.categories.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.dto.CategoryResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.dto.CreateCategoryRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.model.Category;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.repository.CategoryRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryRepository categoryRepository;

    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public CategoryResponse create(CreateCategoryRequest request) {
        Category category = new Category();

        category.setName(request.name());
        category.setDescription(request.description());
        category.setSlug(request.slug());

        Category createdCategory = categoryRepository.save(category);

        return mapToCategoryResponse(createdCategory);
    }

    @Override
    public CategoryResponse findById(Long id) {
        return null;
    }

    @Override
    public CategoryResponse findBySlug(String slug) {
        return null;
    }

    @Override
    public List<CategoryResponse> findAllCategories() {
        return List.of();
    }

    @Override
    public CategoryResponse updateById(Long id, CreateCategoryRequest request) {
        return null;
    }

    @Override
    public String deleteById(Long id) {
        return "";
    }

    private CategoryResponse mapToCategoryResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getSlug(),
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}
