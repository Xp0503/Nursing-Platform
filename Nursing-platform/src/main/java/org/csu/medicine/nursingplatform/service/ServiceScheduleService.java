package org.csu.medicine.nursingplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.csu.medicine.nursingplatform.entity.HealthcareService;
import org.csu.medicine.nursingplatform.entity.ServiceSchedule;
import org.csu.medicine.nursingplatform.mapper.ServiceMapper;
import org.csu.medicine.nursingplatform.mapper.ServiceScheduleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Component
@Service
public class ServiceScheduleService {private static final Logger logger = LoggerFactory.getLogger(ServiceScheduleService.class);

    @Autowired
    private ServiceScheduleMapper scheduleMapper;

    @Autowired
    private ServiceMapper healthcareServiceMapper;


    /**
     * 删除服务预约时间
     */
    @Transactional
    public void deleteSchedule(Long scheduleId) {
        ServiceSchedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("预约时间不存在");
        }

        Long serviceId = schedule.getServiceId();
        scheduleMapper.deleteById(scheduleId);

        // 更新服务状态（检查是否有可用预约时间）
        updateServiceStatus(serviceId);
    }

    /**
     * 更新服务状态
     */
    @Transactional
    public void updateServiceStatus(Long serviceId) {
        int availableSchedules = countAvailableSchedules(serviceId);

        HealthcareService service = new HealthcareService();
        service.setId(serviceId);
        service.setStatus(availableSchedules > 0 ? 1 : 0); // 有可用时间则上架，否则下架

        healthcareServiceMapper.updateById(service);

        logger.info("服务 {} 状态已更新: {}", serviceId, availableSchedules > 0 ? "上架" : "下架");
    }

    /**
     * 计算可用预约时间数量
     */
    private int countAvailableSchedules(Long serviceId) {
        return scheduleMapper.countAvailableSchedules(
                serviceId,
                LocalDate.now(),
                LocalTime.now()
        );
    }

    /**
     * 验证日期和时间有效性
     */
    private void validateScheduleDateTime(ServiceSchedule schedule) {
        // 验证日期是否在今天或未来
        if (schedule.getScheduleDate().isBefore(LocalDate.now())) {
            throw new IllegalArgumentException("预约日期不能是过去日期");
        }

        // 验证小时有效性 (0-23)
        if (schedule.getScheduleHour() == null ||
                schedule.getScheduleHour() < 0 ||
                schedule.getScheduleHour() > 23) {
            throw new IllegalArgumentException("小时必须在0-23之间");
        }

        // 验证分钟有效性 (0-59)
        if (schedule.getScheduleMinute() == null ||
                schedule.getScheduleMinute() < 0 ||
                schedule.getScheduleMinute() > 59) {
            throw new IllegalArgumentException("分钟必须在0-59之间");
        }

        // 验证是否为未来时间
        LocalDateTime scheduledTime = schedule.getStartTime();
        if (scheduledTime.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("预约时间必须是未来时间");
        }
    }



    /**
     * 获取服务的预约时间列表
     */
    public List<ServiceSchedule> getSchedulesByServiceId(Long serviceId) {
        return scheduleMapper.findUpcomingSchedulesByServiceId(
                serviceId,
                LocalDate.now(),
                LocalTime.now()
        );
    }

    /**
     * 获取服务未来7天的预约时间
     */
    public List<ServiceSchedule> getSchedulesForNext7Days(Long serviceId) {
        LocalDate today = LocalDate.now();
        LocalDate endDate = today.plusDays(7);
        return scheduleMapper.findSchedulesByServiceIdAndDateRange(
                serviceId,
                today,
                endDate
        );
    }

    /**
     * 占用预约时间（将状态改为0-不可用）
     * 并检查服务是否还有可用预约时间
     */
    @Transactional
    public void occupySchedule(Long scheduleId) {
        // 1. 验证预约时间存在且可用
        ServiceSchedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("预约时间不存在");
        }
        if (schedule.getStatus() == 0) {
            throw new RuntimeException("预约时间已被占用");
        }

        // 2. 验证预约时间尚未过期
        LocalDateTime scheduledTime = schedule.getStartTime();
        if (scheduledTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("预约时间已过期");
        }

        // 3. 更新预约时间为不可用状态
        ServiceSchedule updateEntity = new ServiceSchedule();
        updateEntity.setId(scheduleId);
        updateEntity.setStatus(0);
        scheduleMapper.updateById(updateEntity);

        // 4. 检查该服务是否还有可用预约时间
        checkAndUpdateServiceStatus(schedule.getServiceId());

        logger.info("预约时间 {} 已被占用，服务 {} 状态更新", scheduleId, schedule.getServiceId());
    }
    /**
     * 检查并更新服务状态
     * 如果服务没有可用预约时间，则下架服务
     */
    @Transactional
    public void checkAndUpdateServiceStatus(Long serviceId) {
        // 检查该服务是否还有可用预约时间
        boolean hasAvailableSchedules = hasAvailableSchedules(serviceId);

        HealthcareService service = new HealthcareService();
        service.setId(serviceId);
        service.setStatus(hasAvailableSchedules ? 1 : 0); // 有可用时间则上架，否则下架

        healthcareServiceMapper.updateById(service);

        logger.info("服务 {} 状态已更新: {}", serviceId, hasAvailableSchedules ? "上架" : "下架");
    }
    /**
     * 检查服务是否还有可用预约时间
     */
    private boolean hasAvailableSchedules(Long serviceId) {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        int count = scheduleMapper.countAvailableSchedulesRaw(
                serviceId,
                today,
                now.getHour(),
                now.getMinute()
        );
        return count > 0;
    }

    /**
     * 释放预约时间（将状态改回1-可用）
     */
    @Transactional
    public void releaseSchedule(Long scheduleId) {
        ServiceSchedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("预约时间不存在");
        }

        // 验证预约时间尚未过期
        LocalDateTime scheduledTime = schedule.getStartTime();
        if (scheduledTime.isBefore(LocalDateTime.now())) {
            throw new RuntimeException("无法释放已过期预约时间");
        }

        // 更新状态为可用
        ServiceSchedule updateEntity = new ServiceSchedule();
        updateEntity.setId(scheduleId);
        updateEntity.setStatus(1);
        scheduleMapper.updateById(updateEntity);

        // 更新服务状态（确保服务重新上架）
        checkAndUpdateServiceStatus(schedule.getServiceId());

        logger.info("预约时间 {} 已释放，服务 {} 状态更新", scheduleId, schedule.getServiceId());
    }

    @Scheduled(cron = "0 */1 * * * ?") // 每1分钟执行
    @Transactional
    public void expireOutdatedSchedules() {
        LocalDate today = LocalDate.now();
        LocalTime now = LocalTime.now();

        // 查询所有当前仍为可用但已过期的预约记录
        List<ServiceSchedule> expiredList = scheduleMapper.findExpiredSchedules(
                today,
                now.getHour(),
                now.getMinute()
        );

        if (expiredList.isEmpty()) return;

        // 批量更新状态为不可用
        int updated = scheduleMapper.markExpiredSchedulesUnavailable(
                today,
                now.getHour(),
                now.getMinute()
        );
        logger.info("自动过期处理：{} 条预约标记为不可用", updated);

        // 对应的 serviceId 列表去重处理
        Set<Long> affectedServiceIds = expiredList.stream()
                .map(ServiceSchedule::getServiceId)
                .collect(Collectors.toSet());

        for (Long sid : affectedServiceIds) {
            checkAndUpdateServiceStatus(sid);
        }
    }

}
