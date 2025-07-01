package org.csu.medicine.nursingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.csu.medicine.nursingplatform.entity.HealthcareService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface ServiceMapper extends BaseMapper<HealthcareService> {

    // 获取所有上架服务
    @Select("SELECT * FROM healthcare_service WHERE status = 1")
    List<HealthcareService> selectActiveServices();

    // 搜索服务
    @Select("SELECT * FROM healthcare_service WHERE status = 1 AND status=1 " +
            "AND (name LIKE CONCAT('%', #{keyword}, '%') " +
            "OR description LIKE CONCAT('%', #{keyword}, '%') " +
            "OR suitable_crowd LIKE CONCAT('%', #{keyword}, '%'))")
    List<HealthcareService> searchServices(String s);
    //按分类获取上架服务
    @Select("SELECT * FROM healthcare_service WHERE category_id = #{categoryId} AND status = 1")
    List<HealthcareService> selectActiveByCategoryId(int i);



    // 继承BaseMapper获得基础CRUD操作
}
