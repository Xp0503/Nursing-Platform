package org.csu.medicine.nursingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.csu.medicine.nursingplatform.entity.ServiceSchedule;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Mapper
public interface ServiceScheduleMapper extends BaseMapper<ServiceSchedule> {
    @Select("SELECT COUNT(*) FROM doctor_schedule " + // 修改表名
            "WHERE service_id = #{serviceId} " +
            "AND status = 1 " +
            "AND start_time > NOW()") // 使用 start_time 和 NOW() 比较
    int countAvailableSchedules(@Param("serviceId") Long serviceId);

    @Select("SELECT * FROM doctor_schedule " + // 修改表名
            "WHERE service_id = #{serviceId} " +
            "AND status = 1 " +
            "AND start_time > NOW() " + // 使用 start_time
            "ORDER BY start_time ASC")
    List<ServiceSchedule> findUpcomingSchedulesByServiceId(
            @Param("serviceId") Long serviceId);

    @Select("SELECT * FROM doctor_schedule " + // 修改表名
            "WHERE service_id = #{serviceId} " +
            "AND start_time BETWEEN #{start} AND #{end} " + // 使用 start_time
            "ORDER BY start_time ASC")
    List<ServiceSchedule> findSchedulesByServiceIdAndDateRange(
            @Param("serviceId") Long serviceId,
            @Param("start") LocalDateTime start,  // 改为 LocalDateTime
            @Param("end") LocalDateTime end);    // 改为 LocalDateTime

    // 简化 countAvailableSchedulesRaw
    @Select("SELECT COUNT(*) FROM doctor_schedule " + // 修改表名
            "WHERE service_id = #{serviceId} " +
            "AND status = 1 " +
            "AND start_time > NOW()")
    int countAvailableSchedulesRaw(@Param("serviceId") Long serviceId);

    @Update("UPDATE doctor_schedule " + // 修改表名
            "SET status = 0 " +
            "WHERE status = 1 " +
            "AND start_time < NOW()") // 使用 start_time
    int markExpiredSchedulesUnavailable();

    @Select("SELECT * FROM doctor_schedule " + // 修改表名
            "WHERE status = 1 " +
            "AND start_time < NOW()") // 使用 start_time
    List<ServiceSchedule> findExpiredSchedules();
}