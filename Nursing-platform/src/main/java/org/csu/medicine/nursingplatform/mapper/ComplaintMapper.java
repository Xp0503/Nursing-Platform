package org.csu.medicine.nursingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.csu.medicine.nursingplatform.entity.Complaint;

import java.util.List;

@Mapper
public interface ComplaintMapper extends BaseMapper<Complaint> {

    @Select("SELECT * FROM complaint WHERE user_id = #{userId} ORDER BY create_time DESC")
    List<Complaint> selectByUserId(@Param("userId") Long userId);

    @Select("SELECT * FROM complaint WHERE order_id = #{orderId} ORDER BY create_time DESC")
    List<Complaint> selectByOrderId(@Param("orderId") Long orderId);
}
