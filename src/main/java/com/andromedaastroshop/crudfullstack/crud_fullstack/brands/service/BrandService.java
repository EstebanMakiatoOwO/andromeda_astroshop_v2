package com.andromedaastroshop.crudfullstack.crud_fullstack.brands.service;

import com.andromedaastroshop.crudfullstack.crud_fullstack.brands.dto.BrandResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.brands.dto.CreateBrandRequest;

import java.util.List;

public interface BrandService {
    List<BrandResponse> findAll();
    BrandResponse findById(Long id);
    BrandResponse create(CreateBrandRequest request);
    BrandResponse update(Long id, CreateBrandRequest request);
    void delete(Long id);
}