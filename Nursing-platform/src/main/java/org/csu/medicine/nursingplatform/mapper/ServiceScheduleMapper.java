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
    @Select("SELECT COUNT(*) FROM service_schedule " +
            "WHERE service_id = #{serviceId} " +
            "AND status = 1 " +
            "AND (schedule_date > #{today} OR " +
            "     (schedule_date = #{today} AND " +
            "      (schedule_hour > #{now.hour} OR " +
            "       (schedule_hour = #{now.hour} AND schedule_minute >= #{now.minute}))))")
    int countAvailableSchedules(
            @Param("serviceId") Long serviceId,
            @Param("today") LocalDate today,
            @Param("now") LocalTime now);

    @Select("SELECT * FROM service_schedule " +
            "WHERE service_id = #{serviceId} " +
            "AND status = 1 " +
            "AND (schedule_date > #{today} OR " +
            "     (schedule_date = #{today} AND " +
            "      (schedule_hour > #{now.hour} OR " +
            "       (schedule_hour = #{now.hour} AND schedule_minute >= #{now.minute})))) " +
            "ORDER BY schedule_date ASC, schedule_hour ASC, schedule_minute ASC")
    List<ServiceSchedule> findUpcomingSchedulesByServiceId(
            @Param("serviceId") Long serviceId,
            @Param("today") LocalDate today,
            @Param("now") LocalTime now);

    @Select("SELECT * FROM service_schedule " +
            "WHERE service_id = #{serviceId} " +
            "AND schedule_date BETWEEN #{start} AND #{end} " +
            "ORDER BY schedule_date ASC, schedule_hour ASC, schedule_minute ASC")
    List<ServiceSchedule> findSchedulesByServiceIdAndDateRange(
            @Param("serviceId") Long serviceId,
            @Param("start") LocalDate start,
            @Param("end") LocalDate end);

    //看看对应service是否有status=1的
    @Select("SELECT COUNT(*) FROM service_schedule " +
            "WHERE service_id = #{serviceId} " +
            "AND status = 1 " +
            "AND (schedule_date > #{today} OR " +
            "     (schedule_date = #{today} AND " +
            "      (schedule_hour > #{nowHour} OR " +
            "       (schedule_hour = #{nowHour} AND schedule_minute >= #{nowMinute}))))")
    int countAvailableSchedulesRaw(
            @Param("serviceId") Long serviceId,
            @Param("today") LocalDate today,
            @Param("nowHour") int nowHour,
            @Param("nowMinute") int nowMinute
    );

    @Update("UPDATE service_schedule " +
            "SET status = 0 " +
            "WHERE status = 1 " +
            "AND (schedule_date < #{today} " +
            "   OR (schedule_date = #{today} AND " +
            "       (schedule_hour < #{hour} " +
            "        OR (schedule_hour = #{hour} AND schedule_minute < #{minute}))))")
    int markExpiredSchedulesUnavailable(
            @Param("today") LocalDate today,
            @Param("hour") int hour,
            @Param("minute") int minute
    );
    //找出有哪些服务的预约已经过期，再更新它们的状态，并根据其 serviceId 更新对应的 healthcare_service 状态。
    @Select("SELECT * FROM service_schedule " +
            "WHERE status = 1 " +
            "AND (schedule_date < #{today} " +
            "  OR (schedule_date = #{today} AND " +
            "      (schedule_hour < #{hour} " +
            "       OR (schedule_hour = #{hour} AND schedule_minute < #{minute}))))")
    List<ServiceSchedule> findExpiredSchedules(
            @Param("today") LocalDate today,
            @Param("hour") int hour,
            @Param("minute") int minute
    );

}
