package com.andromedaastroshop.crudfullstack.crud_fullstack.products.repository;

import com.andromedaastroshop.crudfullstack.crud_fullstack.products.model.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {
}