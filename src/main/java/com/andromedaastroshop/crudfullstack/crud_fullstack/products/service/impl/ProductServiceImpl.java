package com.andromedaastroshop.crudfullstack.crud_fullstack.products.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.dto.CategoryResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.model.Category;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.repository.CategoryRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceAlreadyExistsException;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceNotFoundException;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.CreateProductRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.ProductResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.UpdateProductRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.model.Product;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.model.ProductImage;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.repository.ProductImageRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.repository.ProductRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.service.ProductService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.storage.StorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final StorageService storageService;

    public ProductServiceImpl(ProductRepository productRepository,
                              ProductImageRepository productImageRepository,
                              CategoryRepository categoryRepository,
                              StorageService storageService) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.categoryRepository = categoryRepository;
        this.storageService = storageService;
    }

    @Override
    public ProductResponse create(CreateProductRequest request, MultipartFile image) {
        String sku = (request.sku() != null) ? request.sku().trim() : null;
        if (sku != null && !sku.isEmpty() && productRepository.existsBySku(sku)) {
            throw new ResourceAlreadyExistsException("Product with SKU " + sku + " already exists");
        }

        String barcode = (request.barcode() != null) ? request.barcode().trim() : null;
        if (barcode != null && !barcode.isEmpty() && productRepository.existsByBarcode(barcode)) {
            throw new ResourceAlreadyExistsException("Product with barcode " + barcode + " already exists");
        }

        Product product = new Product();
        product.setSku(request.sku());
        product.setBarcode(request.barcode());
        product.setName(request.name());
        product.setShortDescription(request.shortDescription());
        product.setLongDescription(request.longDescription());
        product.setStock(request.stock() != null ? request.stock() : 0);
        product.setStockAlertThreshold(request.stockAlertThreshold());
        product.setCostPrice(request.costPrice());
        product.setPrice(request.price());
        product.setIsActive(request.isActive());
        product.setIsCatalog(Boolean.TRUE.equals(request.isCatalog()));
        product.setCategories(resolveCategories(request.categoryIds()));

        Product savedProduct = productRepository.save(product);

        if (image != null && !image.isEmpty()) {
            String url = storageService.upload(image);
            ProductImage productImage = new ProductImage();
            productImage.setProduct(savedProduct);
            productImage.setUrl(url);
            productImageRepository.save(productImage);
        }

        return mapToProductResponse(productRepository.findById(savedProduct.getId()).orElseThrow());
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

        String newBarcode = (request.barcode() != null) ? request.barcode().trim() : null;
        if (newBarcode != null && !newBarcode.equals(product.getBarcode())) {
            if (productRepository.existsByBarcode(newBarcode)) {
                throw new ResourceAlreadyExistsException("Product with barcode " + newBarcode + " already exists");
            }
            product.setBarcode(newBarcode);
        }

        product.setName(request.name());
        product.setShortDescription(request.shortDescription());
        product.setLongDescription(request.longDescription());
        product.setStock(request.stock() != null ? request.stock() : 0);
        product.setStockAlertThreshold(request.stockAlertThreshold());
        product.setCostPrice(request.costPrice());
        product.setPrice(request.price());
        product.setIsActive(request.isActive());
        product.setIsCatalog(Boolean.TRUE.equals(request.isCatalog()));
        product.setCategories(resolveCategories(request.categoryIds()));

        Product updatedProduct = productRepository.save(product);

        if (image != null && !image.isEmpty()) {
            String url = storageService.upload(image);
            ProductImage productImage = new ProductImage();
            productImage.setProduct(updatedProduct);
            productImage.setUrl(url);
            productImageRepository.save(productImage);
        }

        return mapToProductResponse(productRepository.findById(updatedProduct.getId()).orElseThrow());
    }

    @Override
    public String deleteById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.getImages().forEach(img -> storageService.delete(img.getUrl()));
        productRepository.delete(product);
        return "Product deleted successfully";
    }

    @Override
    public List<ProductResponse> findByCategory(Long categoryId) {
        return productRepository.findByCategoriesId(categoryId).stream()
                .map(this::mapToProductResponse)
                .toList();
    }

    @Override
    public ProductResponse updateCategories(Long id, List<Long> categoryIds) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Product not found with id: " + id));

        product.setCategories(resolveCategories(categoryIds));
        productRepository.save(product);

        return mapToProductResponse(productRepository.findById(id).orElseThrow());
    }

    private Set<Category> resolveCategories(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) return new HashSet<>();
        return new HashSet<>(categoryRepository.findAllById(categoryIds));
    }

    private ProductResponse mapToProductResponse(Product product) {
        List<CategoryResponse> categories = product.getCategories().stream()
                .map(category -> new CategoryResponse(
                        category.getId(),
                        category.getName(),
                        category.getDescription(),
                        category.getSlug(),
                        category.getCreatedAt(),
                        category.getUpdatedAt()
                ))
                .toList();

        List<String> images = product.getImages().stream()
                .map(ProductImage::getUrl)
                .toList();

        return new ProductResponse(
                product.getId(),
                product.getSku(),
                product.getBarcode(),
                product.getCreatedAt(),
                product.getUpdatedAt(),
                product.getName(),
                product.getShortDescription(),
                product.getLongDescription(),
                product.getStock(),
                product.getStockAlertThreshold(),
                product.getCostPrice(),
                product.getPrice(),
                product.getIsActive(),
                product.getIsCatalog(),
                images,
                categories
        );
    }
}