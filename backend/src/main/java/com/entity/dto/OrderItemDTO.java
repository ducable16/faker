package com.entity.dto;

import com.enums.DeliveryMethod;
import com.enums.OrderStatus;
import com.enums.PaymentMethod;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class OrderItemDTO {

    private Integer productId;
    private Integer variantId;
    private String productName;
    private String description;
    private String specifications;
    private Double weight;
    private Long price;
    private String color;
    private String imageUrl;
    private Integer quantity;
}
