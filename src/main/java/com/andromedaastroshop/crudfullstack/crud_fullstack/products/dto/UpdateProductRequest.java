package com.andromedaastroshop.crudfullstack.crud_fullstack.products.dto;

import java.math.BigDecimal;

public record UpdateProductRequest(String name, String shortDescription, String longDescription, Integer stock, BigDecimal price) {
}
