package com.andromedaastroshop.crudfullstack.crud_fullstack.products.repository;

import com.andromedaastroshop.crudfullstack.crud_fullstack.products.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock - :quantity WHERE p.id = :id AND p.stock >= :quantity")
    int decrementStock(@Param("id") Long id, @Param("quantity") int quantity);

    @Modifying
    @Query("UPDATE Product p SET p.stock = p.stock + :quantity WHERE p.id = :id")
    void incrementStock(@Param("id") Long id, @Param("quantity") int quantity);

    Optional<Product> findBySku(String sku);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findAllByOrderByCreatedAtDesc();

    boolean existsBySku(String sku);

    boolean existsByBarcode(String barcode);

    List<Product> findByCategoriesId(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.isActive = true AND (p.stock = 0 OR (p.stockAlertThreshold IS NOT NULL AND p.stock <= p.stockAlertThreshold)) ORDER BY p.stock ASC")
    List<Product> findLowStockProducts();
}
