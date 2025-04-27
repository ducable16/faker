package com.request;

import com.entity.Product;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;

@Data
public class ProductVariantRequest {

    private String color;

    private Integer discountPercentage;

    private Integer stockQuantity;

    private String imageUrl;
}
