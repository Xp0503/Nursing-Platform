package org.csu.medicine.nursingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.csu.medicine.nursingplatform.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    @Select("SELECT * FROM service_order WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Order> findUserOrders(@Param("userId") Long userId);

    @Update("UPDATE service_order SET status = #{status}, update_time = NOW() WHERE id = #{orderId}")
    int updateOrderStatus(@Param("orderId") Long orderId, @Param("status") Integer status);

    @Update("UPDATE doctor_schedule SET status = #{status} WHERE id = #{scheduleId}")
    void updateScheduleStatus(@Param("scheduleId") Long scheduleId, @Param("status") Integer status);

    @Select("SELECT doctor_id FROM doctor_schedule WHERE id = #{scheduleId}")
    Long getDoctorBySchedule(@Param("scheduleId") Long scheduleId);

    // 新增：查询待处理的订单
    @Select("SELECT * FROM service_order " +
            "WHERE status IN (0, 1, 2) AND update_time < #{threshold}")
    List<Order> findOrdersToProcess(@Param("threshold") LocalDateTime threshold);
}