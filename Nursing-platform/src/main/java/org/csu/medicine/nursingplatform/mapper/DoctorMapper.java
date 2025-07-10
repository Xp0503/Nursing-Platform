package org.csu.medicine.nursingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.csu.medicine.nursingplatform.entity.Doctor;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface DoctorMapper extends BaseMapper<Doctor> {
    @Select("SELECT * FROM doctor WHERE phone = #{phone}")
    Doctor selectByPhone(@Param("phone") String phone);
}