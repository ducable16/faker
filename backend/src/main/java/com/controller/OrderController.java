package com.controller;

import com.entity.Order;
import com.entity.User;
import com.enums.OrderStatus;
import com.enums.Role;
import com.repository.OrderRepository;
import com.request.OrderRequest;
import com.response.StatusResponse;
import com.response.UserInfoResponse;
import com.service.JwtService;
import com.service.OrderService;
import com.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService orderService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtService jwtService;

    @PostMapping("/create")
    public ResponseEntity<?> createOrder(@RequestBody OrderRequest request, @RequestHeader("Authorization") String token) {
        if(orderService.createOrder(request, token)) {
            return ResponseEntity.status(200).body("Order created successfully");
        }
        else return ResponseEntity.status(400).body("Order creation failed");
    }
    @GetMapping("/history/{username}")
    public ResponseEntity<?> viewOrderHistory(@PathVariable String username, @RequestHeader("Authorization") String token) {
        Optional<User> u = userService.getInfo(token);
        if(u.isPresent()) {
            if(u.get().getUsername().equals(username) || jwtService.extractRole(token).equals("ADMIN")) {
                return ResponseEntity.status(200).body(orderService.getOrderHistory(u.get().getUserId()));
            }
            else return ResponseEntity.status(403).body(new StatusResponse("Access Denied"));
        }
        else return ResponseEntity.status(404).body(new StatusResponse("User not found"));
    }
    @GetMapping("/view/{orderId}")
    public ResponseEntity<?> viewOrders(@PathVariable Integer orderId, @RequestHeader("Authorization") String token) {
        Optional<User> u = userService.getInfo(token);
        Optional<Order> order = orderService.getOrderById(orderId);
        if(u.isPresent() && order.isPresent()) {
            if(u.get().getUserId().equals(order.get().getUserId()) || jwtService.extractRole(token).equals(Role.ADMIN) || jwtService.extractRole(token).equals(Role.PRODUCT_MANAGER)) {
                return ResponseEntity.status(200).body(orderService.getOrderHistory(u.get().getUserId()));
            }
            else return ResponseEntity.status(403).body(new StatusResponse("Access Denied"));
        }
        else return ResponseEntity.status(404).body(new StatusResponse("User not found"));

    }
    @GetMapping("/status/{status}")
    public ResponseEntity<?> viewOrders(@PathVariable("status") String status) {
        try {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            List<Order> orders = orderService.getOrderByStatus(orderStatus);
            return ResponseEntity.status(200).body(orders);
        }
        catch (IllegalArgumentException e) {
            return ResponseEntity.status(400).body(new StatusResponse("Invalid order status"));
        }

    }
    @PostMapping("/approve/{orderId}")
    public ResponseEntity<?> approveOrder(@PathVariable Integer orderId) {
        Optional<Order> order = orderService.getOrderById(orderId);
        if(order.isPresent()) {
            order.get().setStatus(OrderStatus.APPROVED);
            return ResponseEntity.status(200).body("Order approved successfully");
        }
        else return ResponseEntity.status(404).body(new StatusResponse("Order not found"));
    }

}
