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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ServiceScheduleService {
    private static final Logger logger = LoggerFactory.getLogger(ServiceScheduleService.class);

    @Autowired
    private ServiceScheduleMapper scheduleMapper;

    @Autowired
    private ServiceMapper healthcareServiceMapper;
    @Autowired
    private ServiceDoctorService serviceDoctorService;

    @Transactional
    public void deleteSchedule(Long scheduleId) {
        ServiceSchedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("预约时间不存在");
        }

        Long serviceId = schedule.getServiceId();
        scheduleMapper.deleteById(scheduleId);

        // 不再更新服务状态
        logger.info("已删除预约时间 {}，服务 {} 状态保持不变", scheduleId, serviceId);
    }

    // 移除updateServiceStatus方法，因为它不再需要

    public List<ServiceSchedule> getSchedulesByServiceId(Long serviceId) {
        return scheduleMapper.findUpcomingSchedulesByServiceId(serviceId);
    }

    public List<ServiceSchedule> getSchedulesForNext7Days(Long serviceId) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime endDate = now.plusDays(7);
        return scheduleMapper.findSchedulesByServiceIdAndDateRange(
                serviceId,
                now,
                endDate
        );
    }

    @Transactional
    public void occupySchedule(Long scheduleId) {
        ServiceSchedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("预约时间不存在");
        }
        if (schedule.getStatus() == 0) {
            throw new RuntimeException("预约时间已被占用");
        }
        if (schedule.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("预约时间已过期");
        }

        ServiceSchedule updateEntity = new ServiceSchedule();
        updateEntity.setId(scheduleId);
        updateEntity.setStatus(0);
        scheduleMapper.updateById(updateEntity);

        // 不再更新服务状态
        logger.info("预约时间 {} 已被占用，服务 {} 状态保持不变", scheduleId, schedule.getServiceId());
    }

    // 移除checkAndUpdateServiceStatus方法，因为它不再需要

    @Transactional
    public void releaseSchedule(Long scheduleId) {
        ServiceSchedule schedule = scheduleMapper.selectById(scheduleId);
        if (schedule == null) {
            throw new RuntimeException("预约时间不存在");
        }

        if (schedule.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("无法释放已过期预约时间");
        }

        ServiceSchedule updateEntity = new ServiceSchedule();
        updateEntity.setId(scheduleId);
        updateEntity.setStatus(1);
        scheduleMapper.updateById(updateEntity);

        // 不再更新服务状态
        logger.info("预约时间 {} 已释放，服务 {} 状态保持不变", scheduleId, schedule.getServiceId());
    }

    @Scheduled(cron = "0 */1 * * * ?")
    @Transactional
    public void expireOutdatedSchedules() {
        List<ServiceSchedule> expiredList = scheduleMapper.findExpiredSchedules();
        if (expiredList.isEmpty()) return;

        int updated = scheduleMapper.markExpiredSchedulesUnavailable();
        logger.info("自动过期处理：{} 条预约标记为不可用，相关服务状态保持不变", updated);
    }

    @Transactional
    public void createSchedule(ServiceSchedule schedule) {
        // 1. 自动建立医生与服务关联（如果不存在）
        if (!serviceDoctorService.existsByServiceAndDoctor(schedule.getServiceId(), schedule.getDoctorId())) {
            serviceDoctorService.addAssociation(schedule.getServiceId(), schedule.getDoctorId());
            logger.info("自动创建医生 {} 与服务 {} 的关联", schedule.getDoctorId(), schedule.getServiceId());
        }

        // 2. 验证时间是否有效
        if (schedule.getStartTime().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("开始时间不能早于当前时间");
        }

        // 3. 设置默认状态为可用
        schedule.setStatus(1);

        // 4. 保存到数据库
        if (scheduleMapper.insert(schedule) != 1) {
            throw new RuntimeException("创建预约时间失败");
        }

        // 5. 不再更新服务状态
        logger.info("为服务 {} 创建了新的预约时间: {}，服务状态保持不变", schedule.getServiceId(), schedule.getStartTime());
    }
}