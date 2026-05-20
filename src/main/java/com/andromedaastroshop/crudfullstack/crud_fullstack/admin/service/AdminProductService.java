package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.service;

import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.AdminProductResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.CreateProductRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.UpdateProductRequest;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface AdminProductService {
    Page<AdminProductResponse> getProducts(int page, int size, String q, String status, String availability, String stock);
    AdminProductResponse getProductById(Long id);
    AdminProductResponse createProduct(CreateProductRequest request, MultipartFile image);
    AdminProductResponse updateProduct(Long id, UpdateProductRequest request, MultipartFile image);
    void deleteProduct(Long id);
}