package com.andromedaastroshop.crudfullstack.crud_fullstack.products.repository;

import com.andromedaastroshop.crudfullstack.crud_fullstack.products.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    Optional<Product> findBySku (String sku);

    Optional<Product> findByName (String name);

    List<Product> findAllByOrderByCreatedAtDesc();

    boolean existsBySku(String sku);
}
