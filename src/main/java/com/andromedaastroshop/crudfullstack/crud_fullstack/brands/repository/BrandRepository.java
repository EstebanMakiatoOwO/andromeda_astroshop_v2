package com.andromedaastroshop.crudfullstack.crud_fullstack.brands.repository;

import com.andromedaastroshop.crudfullstack.crud_fullstack.brands.model.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {
    boolean existsByName(String name);
}