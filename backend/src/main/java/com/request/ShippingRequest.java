package com.request;

import com.enums.DeliveryMethod;
import lombok.Data;

@Data
public class ShippingRequest {
    private String shippingAddress;
    private Item[] items;
    private String deliveryMethod;
}
