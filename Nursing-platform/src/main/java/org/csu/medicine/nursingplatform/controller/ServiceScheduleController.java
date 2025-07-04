package org.csu.medicine.nursingplatform.controller;

import org.csu.medicine.nursingplatform.entity.ServiceSchedule;
import org.csu.medicine.nursingplatform.service.ServiceScheduleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin
public class ServiceScheduleController {

    @Autowired
    private ServiceScheduleService scheduleService;



    // 获取服务的预约时间列表
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<ServiceSchedule>> getSchedulesByServiceId(
            @PathVariable Long serviceId) {
        return ResponseEntity.ok(scheduleService.getSchedulesByServiceId(serviceId));
    }

    // 获取服务未来7天的预约时间
    @GetMapping("/service/{serviceId}/next7days")
    public ResponseEntity<List<ServiceSchedule>> getSchedulesForNext7Days(
            @PathVariable Long serviceId) {
        return ResponseEntity.ok(scheduleService.getSchedulesForNext7Days(serviceId));
    }
    @PostMapping("/{id}/occupy")
    public ResponseEntity<Map<String, Object>> occupySchedule(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            scheduleService.occupySchedule(id);
            result.put("success", true);
            result.put("message", "预约时间已成功占用");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }


    @PostMapping("/{id}/release")
    public ResponseEntity<Map<String, Object>> releaseSchedule(@PathVariable Long id) {
        Map<String, Object> result = new HashMap<>();
        try {
            scheduleService.releaseSchedule(id);
            result.put("success", true);
            result.put("message", "预约时间已成功释放");
            return ResponseEntity.ok(result);
        } catch (RuntimeException e) {
            result.put("success", false);
            result.put("message", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(result);
        }
    }


}
