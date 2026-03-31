package com.andromedaastroshop.crudfullstack.crud_fullstack.products.service;

import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.CreateProductRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.ProductResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.UpdateProductRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {

    ProductResponse create(CreateProductRequest request, MultipartFile image);

    ProductResponse findByid(Long id);

    List<ProductResponse> findAllProducts();

    ProductResponse updateById(Long id, UpdateProductRequest request);

    String deleteById(Long id);
}
