package com.andromedaastroshop.crudfullstack.crud_fullstack.products.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceAlreadyExistsException;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceNotFoundException;
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
        String sku = (request.sku() != null) ? request.sku().trim() : null;
        if (sku != null && !sku.isEmpty() && productRepository.existsBySku(sku)) {
            throw new ResourceAlreadyExistsException("Product with SKU " + request.sku() + " already exists");
        }

        Product product = new Product();
        product.setName(request.name());
        product.setShortDescription(request.shortDescription());
        product.setLongDescription(request.longDescription());
        product.setStock(request.stock());
        product.setPrice(request.price());
        product.setSku(request.sku());

        if (image != null && !image.isEmpty()) {
            String url = storageService.upload(image);
            product.setImgUrl(url);
        }

        Product createProduct = productRepository.save(product);

        return mapToProductResponse(createProduct);
    }

    @Override
    public ProductResponse findById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        return mapToProductResponse(product);
    }

    @Override
    public ProductResponse findBySku(String sku) {
        return productRepository.findBySku(sku)
                .map(this::mapToProductResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with SKU: " + sku));
    }

    @Override
    public List<ProductResponse> findByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    @Override
    public List<ProductResponse> findAllProducts() {
        return productRepository.findAllByOrderByCreatedAtDesc().stream().map(this::mapToProductResponse).toList();
    }

    @Override
    public ProductResponse updateById(Long id, UpdateProductRequest request, MultipartFile image) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        String newSku = (request.sku() != null) ? request.sku().trim() : null;

        if (newSku != null && !newSku.equals(product.getSku())) {
            if (productRepository.existsBySku(newSku)) {
                throw new ResourceAlreadyExistsException("Product with SKU " + newSku + " already exists");
            }
            product.setSku(newSku);
        }

        product.setName(request.name());
        product.setPrice(request.price());
        product.setStock(request.stock());
        product.setShortDescription(request.shortDescription());
        product.setLongDescription(request.longDescription());

        if (image != null && !image.isEmpty()) {
            String url = storageService.upload(image);
            product.setImgUrl(url);
        }

        Product updatedProduct = productRepository.save(product);
        return mapToProductResponse(updatedProduct);
    }

    @Override
    public String deleteById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        productRepository.delete(product);
        return "Product deleted successfully";
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