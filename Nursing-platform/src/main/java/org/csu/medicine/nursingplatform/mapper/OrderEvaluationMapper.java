package org.csu.medicine.nursingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.csu.medicine.nursingplatform.entity.OrderEvaluation;

import java.util.List;

@Mapper
public interface OrderEvaluationMapper extends BaseMapper<OrderEvaluation> {

    @Select("SELECT * FROM order_evaluation WHERE order_id = #{orderId} AND is_deleted = 0")
    OrderEvaluation selectByOrderId(@Param("orderId") Long orderId);

    @Select("SELECT * FROM order_evaluation WHERE user_id = #{userId} AND is_deleted = 0 ORDER BY create_time DESC")
    List<OrderEvaluation> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT e.* FROM order_evaluation e " +
            "JOIN service_order o ON e.order_id = o.id " +
            "WHERE o.service_id = #{serviceId} AND e.is_deleted = 0")
    List<OrderEvaluation> selectByServiceId(@Param("serviceId") Long serviceId);
}
