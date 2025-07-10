package org.csu.medicine.nursingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.*;
import org.csu.medicine.nursingplatform.entity.ServiceDoctor;

import java.util.List;

@Mapper
public interface ServiceDoctorMapper extends BaseMapper<ServiceDoctor> {

    @Select("SELECT EXISTS(SELECT 1 FROM service_doctor WHERE service_id = #{serviceId} AND doctor_id = #{doctorId})")
    boolean existsByServiceAndDoctor(@Param("serviceId") Long serviceId, @Param("doctorId") Long doctorId);

    @Select("SELECT doctor_id FROM service_doctor WHERE service_id = #{serviceId}")
    List<Long> findDoctorIdsByServiceId(@Param("serviceId") Long serviceId);

    @Select("SELECT service_id FROM service_doctor WHERE doctor_id = #{doctorId}")
    List<Long> findServiceIdsByDoctorId(@Param("doctorId") Long doctorId);

    @Delete("DELETE FROM service_doctor WHERE service_id = #{serviceId} AND doctor_id = #{doctorId}")
    int deleteAssociation(@Param("serviceId") Long serviceId, @Param("doctorId") Long doctorId);

    @Insert("INSERT INTO service_doctor(service_id, doctor_id, create_time) VALUES(#{serviceId}, #{doctorId}, NOW())")
    int insertAssociation(@Param("serviceId") Long serviceId, @Param("doctorId") Long doctorId);

    @Delete("DELETE FROM service_doctor WHERE service_id = #{serviceId}")
    int deleteByServiceId(@Param("serviceId") Long serviceId);

    @Delete("DELETE FROM service_doctor WHERE doctor_id = #{doctorId}")
    int deleteByDoctorId(@Param("doctorId") Long doctorId);
}