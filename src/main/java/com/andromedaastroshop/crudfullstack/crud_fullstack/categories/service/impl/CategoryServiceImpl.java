package com.andromedaastroshop.crudfullstack.crud_fullstack.categories.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.dto.CategoryResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.dto.CreateCategoryRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.model.Category;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.repository.CategoryRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.service.CategoryService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceAlreadyExistsException;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceNotFoundException;
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
        if (categoryRepository.existsByName(request.name())) {
            throw new ResourceAlreadyExistsException("Category with name '" + request.name() + "' already exists");
        }
        if (request.slug() != null && categoryRepository.existsBySlug(request.slug())) {
            throw new ResourceAlreadyExistsException("Category with slug '" + request.slug() + "' already exists");
        }

        Category category = new Category();
        category.setName(request.name());
        category.setDescription(request.description());
        category.setSlug(request.slug());

        return mapToCategoryResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse findById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        return mapToCategoryResponse(category);
    }

    @Override
    public CategoryResponse findBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .map(this::mapToCategoryResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
    }

    @Override
    public List<CategoryResponse> findAllCategories() {
        return categoryRepository.findAllByOrderByCreatedAtDesc().stream()
                .map(this::mapToCategoryResponse)
                .toList();
    }

    @Override
    public CategoryResponse updateById(Long id, CreateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));

        if (!category.getName().equals(request.name()) && categoryRepository.existsByName(request.name())) {
            throw new ResourceAlreadyExistsException("Category with name '" + request.name() + "' already exists");
        }
        if (request.slug() != null && !request.slug().equals(category.getSlug()) && categoryRepository.existsBySlug(request.slug())) {
            throw new ResourceAlreadyExistsException("Category with slug '" + request.slug() + "' already exists");
        }

        category.setName(request.name());
        category.setDescription(request.description());
        category.setSlug(request.slug());

        return mapToCategoryResponse(categoryRepository.save(category));
    }

    @Override
    public String deleteById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        categoryRepository.delete(category);
        return "Category deleted successfully";
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
