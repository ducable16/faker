package com.request;

import com.enums.DeliveryMethod;
import com.enums.PaymentMethod;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class OrderRequest {

    private String shippingAddress;

    private String status;

    private PaymentMethod paymentMethod;

    private DeliveryMethod deliveryMethod;

    private String note;

    private Item[] items;
}
