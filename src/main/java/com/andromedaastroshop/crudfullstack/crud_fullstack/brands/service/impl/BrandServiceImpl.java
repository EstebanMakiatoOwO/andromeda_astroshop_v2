package com.andromedaastroshop.crudfullstack.crud_fullstack.brands.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.brands.dto.BrandResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.brands.dto.CreateBrandRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.brands.model.Brand;
import com.andromedaastroshop.crudfullstack.crud_fullstack.brands.repository.BrandRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.brands.service.BrandService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceAlreadyExistsException;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrandServiceImpl implements BrandService {

    private final BrandRepository brandRepository;

    public BrandServiceImpl(BrandRepository brandRepository) {
        this.brandRepository = brandRepository;
    }

    @Override
    public List<BrandResponse> findAll() {
        return brandRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    public BrandResponse findById(Long id) {
        return toResponse(brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand no encontrada con id: " + id)));
    }

    @Override
    public BrandResponse create(CreateBrandRequest request) {
        if (brandRepository.existsByName(request.name())) {
            throw new ResourceAlreadyExistsException("Ya existe una brand con el nombre: " + request.name());
        }
        Brand brand = new Brand();
        brand.setName(request.name());
        brand.setDescription(request.description());
        brand.setLogoUrl(request.logoUrl());
        return toResponse(brandRepository.save(brand));
    }

    @Override
    public BrandResponse update(Long id, CreateBrandRequest request) {
        Brand brand = brandRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Brand no encontrada con id: " + id));
        if (!brand.getName().equals(request.name()) && brandRepository.existsByName(request.name())) {
            throw new ResourceAlreadyExistsException("Ya existe una brand con el nombre: " + request.name());
        }
        brand.setName(request.name());
        brand.setDescription(request.description());
        brand.setLogoUrl(request.logoUrl());
        return toResponse(brandRepository.save(brand));
    }

    @Override
    public void delete(Long id) {
        if (!brandRepository.existsById(id)) {
            throw new ResourceNotFoundException("Brand no encontrada con id: " + id);
        }
        brandRepository.deleteById(id);
    }

    private BrandResponse toResponse(Brand brand) {
        return new BrandResponse(brand.getId(), brand.getName(), brand.getDescription(), brand.getLogoUrl());
    }
}