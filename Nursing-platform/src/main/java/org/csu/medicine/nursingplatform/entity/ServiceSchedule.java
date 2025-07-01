package org.csu.medicine.nursingplatform.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Data
@TableName("service_schedule")
public class ServiceSchedule {
    @TableId(type = IdType.AUTO)
    private Long id;

    private Long serviceId;

    @TableField("schedule_date")
    private LocalDate scheduleDate; // 服务日期

    @TableField("schedule_hour")
    private Integer scheduleHour; // 开始小时 (0-23)

    @TableField("schedule_minute")
    private Integer scheduleMinute; // 开始分钟 (0-59)

    private Integer duration;
    private Integer status; // 0-不可用 1-可用

    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime createdTime;

    @TableField(fill = FieldFill.INSERT_UPDATE)
    private LocalDateTime updatedTime;

    // 获取完整的开始时间（计算属性）
    public LocalDateTime getStartTime() {
        return LocalDateTime.of(
                scheduleDate,
                LocalTime.of(scheduleHour, scheduleMinute)
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getServiceId() {
        return serviceId;
    }

    public void setServiceId(Long serviceId) {
        this.serviceId = serviceId;
    }

    public LocalDate getScheduleDate() {
        return scheduleDate;
    }

    public void setScheduleDate(LocalDate scheduleDate) {
        this.scheduleDate = scheduleDate;
    }

    public Integer getScheduleHour() {
        return scheduleHour;
    }

    public void setScheduleHour(Integer scheduleHour) {
        this.scheduleHour = scheduleHour;
    }

    public Integer getScheduleMinute() {
        return scheduleMinute;
    }

    public void setScheduleMinute(Integer scheduleMinute) {
        this.scheduleMinute = scheduleMinute;
    }

    public Integer getDuration() {
        return duration;
    }

    public void setDuration(Integer duration) {
        this.duration = duration;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public LocalDateTime getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(LocalDateTime createdTime) {
        this.createdTime = createdTime;
    }

    public LocalDateTime getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(LocalDateTime updatedTime) {
        this.updatedTime = updatedTime;
    }
}
