package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.AdminCategoryResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.service.AdminCategoryService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.dto.CreateCategoryRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.model.Category;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.repository.CategoryRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceAlreadyExistsException;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class AdminCategoryServiceImpl implements AdminCategoryService {

    private final CategoryRepository categoryRepository;

    public AdminCategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override
    public List<AdminCategoryResponse> getAll() {
        return categoryRepository.findAllByOrderBySortOrderAscNameAsc().stream()
                .map(c -> toAdminResponse(c, List.of()))
                .toList();
    }

    @Override
    public List<AdminCategoryResponse> getTree() {
        List<Category> all = categoryRepository.findAllByOrderBySortOrderAscNameAsc();
        return buildTree(all, null);
    }

    @Override
    public AdminCategoryResponse getById(Long id) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));
        return toAdminResponse(category, List.of());
    }

    @Override
    @Transactional
    public AdminCategoryResponse create(CreateCategoryRequest request) {
        if (categoryRepository.existsByName(request.name())) {
            throw new ResourceAlreadyExistsException("Ya existe una categoría con el nombre: " + request.name());
        }
        if (request.slug() != null && categoryRepository.existsBySlug(request.slug())) {
            throw new ResourceAlreadyExistsException("Ya existe una categoría con el slug: " + request.slug());
        }
        Category category = applyRequest(new Category(), request);
        return toAdminResponse(categoryRepository.save(category), List.of());
    }

    @Override
    @Transactional
    public AdminCategoryResponse update(Long id, CreateCategoryRequest request) {
        Category category = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada con id: " + id));

        if (!category.getName().equals(request.name()) && categoryRepository.existsByName(request.name())) {
            throw new ResourceAlreadyExistsException("Ya existe una categoría con el nombre: " + request.name());
        }
        if (request.slug() != null && !request.slug().equals(category.getSlug()) && categoryRepository.existsBySlug(request.slug())) {
            throw new ResourceAlreadyExistsException("Ya existe una categoría con el slug: " + request.slug());
        }
        if (request.parentId() != null && request.parentId().equals(id)) {
            throw new IllegalArgumentException("Una categoría no puede ser su propio padre");
        }

        return toAdminResponse(categoryRepository.save(applyRequest(category, request)), List.of());
    }

    @Override
    @Transactional
    public void delete(Long id) {
        if (!categoryRepository.existsById(id)) {
            throw new ResourceNotFoundException("Categoría no encontrada con id: " + id);
        }
        if (categoryRepository.existsByParentId(id)) {
            throw new IllegalStateException("No se puede eliminar una categoría que tiene subcategorías");
        }
        categoryRepository.deleteProductCategoryRelations(id);
        categoryRepository.deleteById(id);
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private List<AdminCategoryResponse> buildTree(List<Category> all, Long parentId) {
        return all.stream()
                .filter(c -> parentId == null
                        ? c.getParent() == null
                        : c.getParent() != null && c.getParent().getId().equals(parentId))
                .sorted(Comparator.comparing(c -> Optional.ofNullable(c.getSortOrder()).orElse(0)))
                .map(c -> toAdminResponse(c, buildTree(all, c.getId())))
                .toList();
    }

    private Category applyRequest(Category category, CreateCategoryRequest request) {
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

    private AdminCategoryResponse toAdminResponse(Category c, List<AdminCategoryResponse> children) {
        Long productCount = categoryRepository.countProductsByCategoryId(c.getId());
        return new AdminCategoryResponse(
                c.getId(),
                c.getName(),
                c.getDescription(),
                c.getSlug(),
                c.getIsActive(),
                c.getSortOrder(),
                c.getImageUrl(),
                c.getMetaTitle(),
                c.getMetaDescription(),
                c.getParent() != null ? c.getParent().getId() : null,
                c.getParent() != null ? c.getParent().getName() : null,
                productCount,
                children,
                c.getCreatedAt(),
                c.getUpdatedAt()
        );
    }
}