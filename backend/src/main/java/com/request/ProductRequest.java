package com.request;

import com.entity.ProductVariant;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ProductRequest {

    private String productName;

    private String description;

    private String specifications;

    private Double weight;

    private Long price;

    private String categoryName;

    private String brandName;

    private Boolean supportRushOrder;

    private List<ProductVariantRequest> variants;

}
