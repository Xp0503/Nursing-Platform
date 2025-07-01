package org.csu.medicine.nursingplatform.controller;


import org.csu.medicine.nursingplatform.entity.Order;
import org.csu.medicine.nursingplatform.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired
    private OrderService orderService;

    /**
     * 获取用户历史订单
     */
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Order>> getUserOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(orderService.getUserOrders(userId));
    }

    /**
     * 提交新订单
     */
    @PostMapping
    public ResponseEntity<Order> createOrder(
            @RequestParam Long userId,
            @RequestParam Long serviceId,
            @RequestParam Long addressId,
            @RequestParam String appointmentTime, // ISO格式: "2025-07-01T10:00:00"
            @RequestParam BigDecimal amount,
            @RequestParam(required = false) String remark) {

        LocalDateTime appointment = LocalDateTime.parse(appointmentTime);
        Order order = orderService.createOrder(userId, serviceId, addressId, appointment, amount, remark);
        return ResponseEntity.ok(order);
    }

    /**
     * 支付成功回调
     */
    @PostMapping("/{orderNo}/pay-success")
    public ResponseEntity<Void> paymentSuccess(
            @PathVariable String orderNo,
            @RequestParam Integer payMethod) {
        orderService.processPaymentSuccess(orderNo, payMethod);
        return ResponseEntity.ok().build();
    }

    /**
     * 取消订单
     */
    @PostMapping("/{orderId}/cancel")
    public ResponseEntity<Void> cancelOrder(@PathVariable Long orderId) {
        orderService.cancelOrder(orderId);
        return ResponseEntity.ok().build();
    }
}