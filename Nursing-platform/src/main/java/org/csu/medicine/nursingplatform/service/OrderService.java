package org.csu.medicine.nursingplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.csu.medicine.nursingplatform.entity.Order;
import org.csu.medicine.nursingplatform.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class OrderService {

    @Autowired
    private OrderMapper orderMapper;

    /**
     * 获取用户历史订单
     */
    public List<Order> getUserOrders(Long userId) {
        return orderMapper.findUserOrders(userId);
    }

    /**
     * 提交订单
     */
    @Transactional
    public Order createOrder(Long userId, Long serviceId, Long addressId,
                             LocalDateTime appointmentTime, BigDecimal amount,
                             String remark, Long scheduleId) {

        // 生成唯一订单号
        String orderNo = "ORD" + UUID.randomUUID().toString().replace("-", "").substring(0, 16);

        Order order = new Order();
        order.setOrderNo(orderNo);
        order.setUserId(userId);
        order.setServiceId(serviceId);
        order.setAddressId(addressId);
        order.setAppointmentTime(appointmentTime);
        order.setAmount(amount);
        order.setRemark(remark);
        order.setStatus(0); // 待支付状态
        order.setScheduleId(scheduleId); // 关联排班ID
        order.setCreateTime(LocalDateTime.now());
        order.setUpdateTime(LocalDateTime.now());
        orderMapper.insert(order);

        // 锁定排班
        orderMapper.updateScheduleStatus(scheduleId, 0); // 0=已预约
        return order;
    }

    /**
     * 支付成功回调
     */
    @Transactional
    public void processPaymentSuccess(String orderNo, Integer payMethod) {
        // 根据订单号查询订单
        QueryWrapper<Order> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("order_no", orderNo);
        Order order = orderMapper.selectOne(queryWrapper);

        if (order == null) {
            throw new RuntimeException("订单不存在: " + orderNo);
        }

        // 更新订单状态
        order.setStatus(1); // 待服务
        order.setPayMethod(payMethod);
        order.setPayTime(LocalDateTime.now());

        // 关联医生
        if (order.getScheduleId() != null) {
            Long doctorId = orderMapper.getDoctorBySchedule(order.getScheduleId());
            order.setDoctorId(doctorId);
        }

        orderMapper.updateById(order);
    }

    /**
     * 取消订单
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 只能取消待支付或待服务的订单
        if (order.getStatus() != 0 && order.getStatus() != 1) {
            throw new RuntimeException("订单当前状态无法取消");
        }

        // 更新状态为已取消
        orderMapper.updateOrderStatus(orderId, 4);

        // 释放排班资源
        if (order.getScheduleId() != null) {
            orderMapper.updateScheduleStatus(order.getScheduleId(), 1); // 1=可预约
        }
    }

    /**
     * 每分钟执行一次的定时任务
     */
    @Scheduled(cron = "0 * * * * ?") // 每分钟执行一次
    @Transactional
    public void processOrderStatus() {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime threshold = now.minusMinutes(1); // 处理1分钟前的订单

        // 获取需要处理的订单
        List<Order> ordersToProcess = orderMapper.findOrdersToProcess(threshold);

        for (Order order : ordersToProcess) {
            switch (order.getStatus()) {
                case 0: // 待支付
                    // 超过30分钟未支付，自动取消
                    if (order.getCreateTime().isBefore(now.minusMinutes(30))) {
                        orderMapper.updateOrderStatus(order.getId(), 4);
                        // 释放排班资源
                        if (order.getScheduleId() != null) {
                            orderMapper.updateScheduleStatus(order.getScheduleId(), 1); // 1=可预约
                        }
                    }
                    break;

                case 1: // 待服务
                    // 超过预约时间，自动开始服务
                    if (order.getAppointmentTime().isBefore(now)) {
                        orderMapper.updateOrderStatus(order.getId(), 2);
                    }
                    break;

                case 2: // 服务中
                    // 服务开始超过1小时，自动完成
                    if (order.getUpdateTime().isBefore(now.minusHours(1))) {
                        orderMapper.updateOrderStatus(order.getId(), 3);
                    }
                    break;
            }
        }
    }
}