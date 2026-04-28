package com.andromedaastroshop.crudfullstack.crud_fullstack.products.repository;

import com.andromedaastroshop.crudfullstack.crud_fullstack.products.model.Product;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT p FROM Product p WHERE p.id = :id")
    Optional<Product> findByIdForUpdate(@Param("id") Long id);

    Optional<Product> findBySku(String sku);

    List<Product> findByNameContainingIgnoreCase(String name);

    List<Product> findAllByOrderByCreatedAtDesc();

    boolean existsBySku(String sku);

    boolean existsByBarcode(String barcode);

    List<Product> findByCategoriesId(Long categoryId);
}
