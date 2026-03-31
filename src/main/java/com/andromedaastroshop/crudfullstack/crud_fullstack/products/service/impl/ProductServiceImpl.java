package com.andromedaastroshop.crudfullstack.crud_fullstack.products.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.CreateProductRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.ProductResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.UpdateProductRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.model.Product;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.repository.ProductRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.service.ProductService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final StorageService storageService;

    public ProductServiceImpl(ProductRepository productRepository, StorageService storageService) {
        this.productRepository = productRepository;
        this.storageService = storageService;
    }

    @Override
    public ProductResponse create(CreateProductRequest request, MultipartFile image) {

        Product product = new Product();
        product.setName(request.name());
        product.setShortDescription(request.shortDescription());
        product.setLongDescription(request.longDescription());
        product.setStock(request.stock());
        product.setPrice(request.price());

        if (image != null && !image.isEmpty()) {
            String url = storageService.upload(image);
            product.setImgUrl(url);
        }

        Product createProduct = productRepository.save(product);

        return mapToProductResponse(createProduct);
    }

    @Override
    public ProductResponse findByid(Long id) {
        return null;
    }

    @Override
    public List<ProductResponse> findAllProducts() {
        return List.of();
    }

    @Override
    public ProductResponse updateById(Long id, UpdateProductRequest request) {
        return null;
    }

    @Override
    public String deleteById(Long id) {
        return "";
    }

    private ProductResponse mapToProductResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getName(),
                product.getShortDescription(),
                product.getLongDescription(),
                product.getStock(),
                product.getPrice(),
                product.getImgUrl()
        );
    }
}