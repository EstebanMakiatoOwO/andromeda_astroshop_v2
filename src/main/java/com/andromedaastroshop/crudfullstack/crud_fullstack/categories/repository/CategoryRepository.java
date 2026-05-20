package com.andromedaastroshop.crudfullstack.crud_fullstack.categories.repository;

import com.andromedaastroshop.crudfullstack.crud_fullstack.categories.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    Optional<Category> findBySlug(String slug);

    List<Category> findAllByOrderByCreatedAtDesc();

    List<Category> findAllByOrderBySortOrderAscNameAsc();

    boolean existsByName(String name);

    boolean existsBySlug(String slug);

    boolean existsByParentId(Long parentId);

    @Query("SELECT COUNT(p) FROM Product p JOIN p.categories c WHERE c.id = :categoryId")
    Long countProductsByCategoryId(@Param("categoryId") Long categoryId);

    @Modifying
    @Query(value = "DELETE FROM product_categories WHERE category_id = :categoryId", nativeQuery = true)
    void deleteProductCategoryRelations(@Param("categoryId") Long categoryId);
}