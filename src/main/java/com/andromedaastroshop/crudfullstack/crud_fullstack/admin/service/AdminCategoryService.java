package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.service;

import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.AdminCategoryResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.dto.CreateCategoryRequest;

import java.util.List;

public interface AdminCategoryService {
    List<AdminCategoryResponse> getAll();
    List<AdminCategoryResponse> getTree();
    AdminCategoryResponse getById(Long id);
    AdminCategoryResponse create(CreateCategoryRequest request);
    AdminCategoryResponse update(Long id, CreateCategoryRequest request);
    void delete(Long id);
}