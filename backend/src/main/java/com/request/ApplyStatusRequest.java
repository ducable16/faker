package com.request;

import com.enums.DeliveryMethod;
import com.enums.OrderStatus;
import com.enums.PaymentMethod;
import lombok.Data;

@Data
public class ApplyStatusRequest {

    private Integer orderId;

    private String status;
}
