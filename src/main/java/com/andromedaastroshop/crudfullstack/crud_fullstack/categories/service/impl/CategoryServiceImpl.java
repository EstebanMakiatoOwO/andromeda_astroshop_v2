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

        Category category = buildCategory(new Category(), request);
        return toResponse(categoryRepository.save(category));
    }

    @Override
    public CategoryResponse findById(Long id) {
        return toResponse(categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id)));
    }

    @Override
    public CategoryResponse findBySlug(String slug) {
        return categoryRepository.findBySlug(slug)
                .map(this::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with slug: " + slug));
    }

    @Override
    public List<CategoryResponse> findAllCategories() {
        return categoryRepository.findAllByOrderBySortOrderAscNameAsc().stream()
                .map(this::toResponse)
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

        return toResponse(categoryRepository.save(buildCategory(category, request)));
    }

    @Override
    public String deleteById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + id));
        categoryRepository.deleteProductCategoryRelations(id);
        categoryRepository.delete(category);
        return "Category deleted successfully";
    }

    private Category buildCategory(Category category, CreateCategoryRequest request) {
        category.setName(request.name());
        category.setDescription(request.description());
        category.setSlug(request.slug());
        category.setIsActive(request.isActive() != null ? request.isActive() : Boolean.TRUE);
        category.setSortOrder(request.sortOrder() != null ? request.sortOrder() : 0);
        category.setImageUrl(request.imageUrl());
        category.setMetaTitle(request.metaTitle());
        category.setMetaDescription(request.metaDescription());
        if (request.parentId() != null) {
            category.setParent(categoryRepository.findById(request.parentId())
                    .orElseThrow(() -> new ResourceNotFoundException("Categoría padre no encontrada con id: " + request.parentId())));
        } else {
            category.setParent(null);
        }
        return category;
    }

    CategoryResponse toResponse(Category category) {
        return new CategoryResponse(
                category.getId(),
                category.getName(),
                category.getDescription(),
                category.getSlug(),
                category.getIsActive(),
                category.getSortOrder(),
                category.getImageUrl(),
                category.getParent() != null ? category.getParent().getId() : null,
                category.getCreatedAt(),
                category.getUpdatedAt()
        );
    }
}