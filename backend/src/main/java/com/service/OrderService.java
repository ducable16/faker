package com.service;

import com.entity.*;
import com.entity.dto.OrderItemDTO;
import com.entity.dto.ProductDTO;
import com.entity.dto.ProductVariantDTO;
import com.enums.DeliveryMethod;
import com.enums.OrderStatus;
import com.repository.OrderItemRepository;
import com.repository.OrderRepository;
import com.repository.ProductRepository;
import com.repository.ProductVariantRepository;
import com.repository.location.DistrictRepository;
import com.repository.location.ProvinceRepository;
import com.request.Item;
import com.request.OrderRequest;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {

    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    UserService userService;
    @Autowired
    private OrderItemRepository orderItemRepository;
    @Autowired
    private ProductVariantRepository productVariantRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private EmailService emailService;

   public Integer createOrder(OrderRequest orderRequest, String token) {
        if(orderRequest.getItems().length == 0) return -1;
        User user = userService.getInfo(token).get();
        Order order = new Order(orderRequest, user.getUserId());
        System.out.println(order.getOrderId());
        Long totalAmount = 0L;
        for(Item item : orderRequest.getItems()) {
            ProductVariant productVariant = productVariantRepository.findById(item.getVariantId()).get();
            Product product = productRepository.findById(item.getProductId()).get();
            totalAmount += priceCalculate(product.getPrice(), productVariant.getDiscountPercentage()) * item.getQuantity();
        }
        order.setTotalAmount(totalAmount);
        order.setNote(orderRequest.getNote());
        order.setShippingFee(orderRequest.getShippingFee());
        System.out.println(order.getStatus());
        orderRepository.save(order);


        for(Item item : orderRequest.getItems()) {
            OrderItem orderItem = new OrderItem(order.getOrderId(), item.getProductId(), item.getVariantId(), item.getQuantity());
            orderItemRepository.save(orderItem);
            cartService.removeCartItemWhenCreateOrder(user.getUserId(), item.getProductId(), item.getVariantId());
        }

        // --- Gửi email xác nhận đơn hàng ---
        try {
            String to = user.getEmail();
            String subject = "Xác nhận đơn hàng #" + order.getOrderId();
            StringBuilder content = new StringBuilder();
            content.append("Chào ").append(user.getFullName()).append(",\n\n");
            content.append("Cảm ơn bạn đã đặt hàng tại cửa hàng của chúng tôi.\n");
            content.append("Thông tin đơn hàng:\n");
            content.append("Mã đơn hàng: ").append(order.getOrderId()).append("\n");
            content.append("Tổng tiền: ").append(totalAmount).append(" VND\n");
            content.append("Phí vận chuyển: ").append(order.getShippingFee()).append(" VND\n");
            content.append("Ghi chú: ").append(order.getNote() != null ? order.getNote() : "Không có").append("\n\n");
            content.append("Chúng tôi sẽ sớm xử lý đơn hàng của bạn.\n");
            content.append("Trân trọng,\nĐội ngũ cửa hàng");

            emailService.sendEmail(to, subject, content.toString());
        } catch (Exception e) {
            System.out.println("Gửi email thất bại: " + e.getMessage());
        }


        return order.getOrderId();
    }
    public Long priceCalculate(Long originalPrice, Integer discountPercentage) {
        return originalPrice * (100 - discountPercentage) / 100;
    }

    public Optional<Order> getOrderById(Integer orderId) {
        return orderRepository.findOrderByOrderId(orderId);
    }

    public List<Order> getOrderHistory(Integer userId) {
        return orderRepository.findOrdersByUserId(userId);
    }

    public List<Order> getOrderByStatus(OrderStatus status) {
        return orderRepository.findOrdersByStatus(status);
    }
    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    public Order applyOrderStatus(Integer orderId, OrderStatus status) {
        Optional<Order> order = orderRepository.findOrderByOrderId(orderId);
        if(order.isPresent()) {
            order.get().setStatus(status);
            orderRepository.save(order.get());
            if(status == OrderStatus.APPROVED) {
//                OrderItem item = new OrderItem(orderId, );
            }
            return order.get();
        }
        else {
            return null;
        }
    }
    public List<OrderItemDTO> getProductsByOrderId(Integer orderId) {
        List<OrderItem> orderItems = orderItemRepository.findByOrderId(orderId);
        List<OrderItemDTO> orderItemDTOs = new ArrayList<>();
        for(OrderItem orderItem : orderItems) {
            OrderItemDTO item = new OrderItemDTO();
            Optional<Product> optionalProduct = productRepository.findByProductId(orderItem.getProductId());
            if(optionalProduct.isEmpty()) {
                item.setProductId(orderItem.getProductId());
            }
            else {
                Product product = optionalProduct.get();
                for(ProductVariant productVariant : product.getVariants()) {
                    if(productVariant.getVariantId() == orderItem.getVariantId()) {
                        item.setVariantId(productVariant.getVariantId());
                        item.setPrice(priceCalculate(product.getPrice(), productVariant.getDiscountPercentage()));
                        item.setColor(productVariant.getColor());
                        item.setImageUrl(productVariant.getImageUrl());
                        break;
                    }
                }
                item.setProductId(product.getProductId());
                item.setProductName(product.getProductName());
                item.setDescription(product.getDescription());
                item.setSpecifications(product.getSpecifications());
                item.setWeight(product.getWeight());
                item.setQuantity(orderItem.getQuantity());
            }
            orderItemDTOs.add(item);
        }
        return orderItemDTOs;
    }

}
