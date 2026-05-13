package com.andromedaastroshop.crudfullstack.crud_fullstack.admin.service.impl;

import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.dto.AdminProductResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.admin.service.AdminProductService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.brands.dto.BrandResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.brands.repository.BrandRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.dto.CategoryResponse;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.model.Category;
import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.repository.CategoryRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.CreateProductRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto.UpdateProductRequest;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.model.Product;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.model.ProductImage;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.model.ProductStatus;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.repository.ProductImageRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.repository.ProductRepository;
import com.andromedaastroshop.crudfullstack.crud_fullstack.products.storage.StorageService;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceAlreadyExistsException;
import com.andromedaastroshop.crudfullstack.crud_fullstack.shared.exception.ResourceNotFoundException;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class AdminProductServiceImpl implements AdminProductService {

    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;
    private final CategoryRepository categoryRepository;
    private final BrandRepository brandRepository;
    private final StorageService storageService;

    public AdminProductServiceImpl(ProductRepository productRepository,
                                   ProductImageRepository productImageRepository,
                                   CategoryRepository categoryRepository,
                                   BrandRepository brandRepository,
                                   StorageService storageService) {
        this.productRepository = productRepository;
        this.productImageRepository = productImageRepository;
        this.categoryRepository = categoryRepository;
        this.brandRepository = brandRepository;
        this.storageService = storageService;
    }

    @Override
    public Page<AdminProductResponse> getProducts(int page, int size, String q, String status, String availability, String stock) {
        Specification<Product> spec = buildSpec(q, status, availability, stock);
        PageRequest pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        return productRepository.findAll(spec, pageable).map(this::toAdminResponse);
    }

    @Override
    public AdminProductResponse getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        return toAdminResponse(product);
    }

    @Override
    @Transactional
    public AdminProductResponse createProduct(CreateProductRequest request, MultipartFile image) {
        String sku = (request.sku() != null) ? request.sku().trim() : null;
        if (sku != null && !sku.isEmpty() && productRepository.existsBySku(sku)) {
            throw new ResourceAlreadyExistsException("Producto con SKU " + sku + " ya existe");
        }

        String barcode = (request.barcode() != null) ? request.barcode().trim() : null;
        if (barcode != null && !barcode.isEmpty() && productRepository.existsByBarcode(barcode)) {
            throw new ResourceAlreadyExistsException("Producto con barcode " + barcode + " ya existe");
        }

        Product product = new Product();
        product.setSku(sku);
        product.setBarcode(barcode);
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
        if (request.brandId() != null) {
            product.setBrand(brandRepository.findById(request.brandId())
                    .orElseThrow(() -> new ResourceNotFoundException("Brand no encontrada con id: " + request.brandId())));
        }

        Product saved = productRepository.save(product);

        if (image != null && !image.isEmpty()) {
            String url = storageService.upload(image);
            ProductImage productImage = new ProductImage();
            productImage.setProduct(saved);
            productImage.setUrl(url);
            productImageRepository.save(productImage);
        }

        return toAdminResponse(productRepository.findById(saved.getId()).orElseThrow());
    }

    @Override
    @Transactional
    public AdminProductResponse updateProduct(Long id, UpdateProductRequest request, MultipartFile image) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        String newSku = (request.sku() != null) ? request.sku().trim() : null;
        if (newSku != null && !newSku.equals(product.getSku())) {
            if (productRepository.existsBySku(newSku)) {
                throw new ResourceAlreadyExistsException("Producto con SKU " + newSku + " ya existe");
            }
            product.setSku(newSku);
        }

        String newBarcode = (request.barcode() != null) ? request.barcode().trim() : null;
        if (newBarcode != null && !newBarcode.equals(product.getBarcode())) {
            if (productRepository.existsByBarcode(newBarcode)) {
                throw new ResourceAlreadyExistsException("Producto con barcode " + newBarcode + " ya existe");
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
        product.setBrand(request.brandId() != null
                ? brandRepository.findById(request.brandId())
                        .orElseThrow(() -> new ResourceNotFoundException("Brand no encontrada con id: " + request.brandId()))
                : null);

        Product updated = productRepository.save(product);

        if (image != null && !image.isEmpty()) {
            String url = storageService.upload(image);
            ProductImage productImage = new ProductImage();
            productImage.setProduct(updated);
            productImage.setUrl(url);
            productImageRepository.save(productImage);
        }

        return toAdminResponse(productRepository.findById(updated.getId()).orElseThrow());
    }

    // ── Specification builders ────────────────────────────────────────────────

    private Specification<Product> buildSpec(String q, String status, String availability, String stock) {
        Specification<Product> spec = Specification.where(null);

        if (q != null && !q.isBlank()) {
            String lower = q.toLowerCase().trim();
            spec = spec.and((root, query, cb) -> cb.or(
                    cb.like(cb.lower(root.get("name")), "%" + lower + "%"),
                    cb.like(cb.lower(root.get("sku")), "%" + lower + "%")
            ));
        }

        if (status != null && !status.isBlank()) {
            spec = spec.and(statusSpec(status));
        }

        if (availability != null && !availability.isBlank()) {
            spec = spec.and(availabilitySpec(availability));
        }

        if (stock != null && !stock.isBlank()) {
            spec = spec.and(stockFilterSpec(stock));
        }

        return spec;
    }

    private Specification<Product> statusSpec(String status) {
        return switch (status.toUpperCase()) {
            case "DRAFT" -> (root, query, cb) ->
                    cb.isFalse(root.get("isActive"));
            case "ON_REQUEST" -> (root, query, cb) ->
                    cb.and(cb.isTrue(root.get("isActive")), cb.isTrue(root.get("isCatalog")));
            case "OUT_OF_STOCK" -> (root, query, cb) ->
                    cb.and(cb.isTrue(root.get("isActive")), cb.isFalse(root.get("isCatalog")), cb.equal(root.get("stock"), 0));
            case "LOW_STOCK" -> (root, query, cb) ->
                    cb.and(
                            cb.isTrue(root.get("isActive")),
                            cb.isFalse(root.get("isCatalog")),
                            cb.greaterThan(root.get("stock"), 0),
                            cb.isNotNull(root.get("stockAlertThreshold")),
                            cb.lessThanOrEqualTo(root.get("stock"), root.get("stockAlertThreshold"))
                    );
            case "ACTIVE" -> (root, query, cb) ->
                    cb.and(
                            cb.isTrue(root.get("isActive")),
                            cb.isFalse(root.get("isCatalog")),
                            cb.greaterThan(root.get("stock"), 0),
                            cb.or(
                                    cb.isNull(root.get("stockAlertThreshold")),
                                    cb.greaterThan(root.get("stock"), root.get("stockAlertThreshold"))
                            )
                    );
            default -> (root, query, cb) -> cb.conjunction();
        };
    }

    private Specification<Product> availabilitySpec(String availability) {
        return switch (availability.toUpperCase()) {
            case "IN_STOCK" -> (root, query, cb) ->
                    cb.and(cb.isFalse(root.get("isCatalog")), cb.greaterThan(root.get("stock"), 0));
            case "ON_REQUEST" -> (root, query, cb) ->
                    cb.isTrue(root.get("isCatalog"));
            default -> (root, query, cb) -> cb.conjunction();
        };
    }

    private Specification<Product> stockFilterSpec(String stockFilter) {
        return switch (stockFilter.toLowerCase()) {
            case "low" -> (root, query, cb) ->
                    cb.and(
                            cb.greaterThan(root.get("stock"), 0),
                            cb.isNotNull(root.get("stockAlertThreshold")),
                            cb.lessThanOrEqualTo(root.get("stock"), root.get("stockAlertThreshold"))
                    );
            case "none" -> (root, query, cb) -> cb.equal(root.get("stock"), 0);
            default -> (root, query, cb) -> cb.conjunction();
        };
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private Set<Category> resolveCategories(List<Long> categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) return new HashSet<>();
        return new HashSet<>(categoryRepository.findAllById(categoryIds));
    }

    private ProductStatus computeStatus(Product p) {
        if (!Boolean.TRUE.equals(p.getIsActive())) return ProductStatus.DRAFT;
        if (Boolean.TRUE.equals(p.getIsCatalog())) return ProductStatus.ON_REQUEST;
        if (p.getStock() == 0) return ProductStatus.OUT_OF_STOCK;
        if (p.getStockAlertThreshold() != null && p.getStock() <= p.getStockAlertThreshold()) return ProductStatus.LOW_STOCK;
        return ProductStatus.ACTIVE;
    }

    private AdminProductResponse toAdminResponse(Product product) {
        List<CategoryResponse> categories = product.getCategories().stream()
                .map(c -> new CategoryResponse(
                        c.getId(), c.getName(), c.getDescription(), c.getSlug(),
                        c.getIsActive(), c.getSortOrder(), c.getImageUrl(),
                        c.getParent() != null ? c.getParent().getId() : null,
                        c.getCreatedAt(), c.getUpdatedAt()))
                .toList();

        List<String> images = product.getImages().stream()
                .map(ProductImage::getUrl)
                .toList();

        BrandResponse brand = product.getBrand() != null
                ? new BrandResponse(product.getBrand().getId(), product.getBrand().getName(),
                        product.getBrand().getDescription(), product.getBrand().getLogoUrl())
                : null;

        return new AdminProductResponse(
                product.getId(),
                product.getSku(),
                product.getBarcode(),
                product.getName(),
                product.getShortDescription(),
                product.getLongDescription(),
                product.getStock(),
                product.getStockAlertThreshold(),
                product.getCostPrice(),
                product.getPrice(),
                product.getIsActive(),
                product.getIsCatalog(),
                computeStatus(product),
                images,
                categories,
                brand,
                product.getCreatedAt(),
                product.getUpdatedAt()
        );
    }
}