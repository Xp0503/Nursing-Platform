package org.csu.medicine.nursingplatform.controller;

import org.csu.medicine.nursingplatform.service.ServiceDoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/service-doctors")
public class ServiceDoctorController {

    private final ServiceDoctorService serviceDoctorService;

    @Autowired
    public ServiceDoctorController(ServiceDoctorService serviceDoctorService) {
        this.serviceDoctorService = serviceDoctorService;
    }

    // 创建服务-医生关联
    @PostMapping
    public ResponseEntity<Map<String, Object>> createAssociation(
            @RequestParam Long serviceId,
            @RequestParam Long doctorId) {
        Map<String, Object> response = new HashMap<>();
        if (serviceDoctorService.addAssociation(serviceId, doctorId)) {
            response.put("success", true);
            response.put("message", "关联创建成功");
            return ResponseEntity.ok(response);
        }
        response.put("success", false);
        response.put("message", "关联已存在");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 删除特定关联
    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteAssociation(
            @RequestParam Long serviceId,
            @RequestParam Long doctorId) {
        Map<String, Object> response = new HashMap<>();
        if (serviceDoctorService.removeAssociation(serviceId, doctorId)) {
            response.put("success", true);
            response.put("message", "关联删除成功");
            return ResponseEntity.ok(response);
        }
        response.put("success", false);
        response.put("message", "关联不存在");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 删除服务的所有医生关联
    @DeleteMapping("/service/{serviceId}")
    public ResponseEntity<Map<String, Object>> deleteAllAssociationsByService(
            @PathVariable Long serviceId) {
        Map<String, Object> response = new HashMap<>();
        if (serviceDoctorService.removeAssociationsByService(serviceId)) {
            response.put("success", true);
            response.put("message", "服务的所有关联已删除");
            return ResponseEntity.ok(response);
        }
        response.put("success", false);
        response.put("message", "删除失败");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    // 获取服务的医生列表
    @GetMapping("/service/{serviceId}/doctors")
    public ResponseEntity<List<Long>> getDoctorsByService(
            @PathVariable Long serviceId) {
        return ResponseEntity.ok(serviceDoctorService.getDoctorsByService(serviceId));
    }

    // 获取医生的服务列表
    @GetMapping("/doctor/{doctorId}/services")
    public ResponseEntity<List<Long>> getServicesByDoctor(
            @PathVariable Long doctorId) {
        return ResponseEntity.ok(serviceDoctorService.getServicesByDoctor(doctorId));
    }

    // 批量添加医生到服务
    @PostMapping("/batch-add")
    public ResponseEntity<Map<String, Object>> batchAddDoctorsToService(
            @RequestParam Long serviceId,
            @RequestBody List<Long> doctorIds) {
        Map<String, Object> response = new HashMap<>();
        int successCount = serviceDoctorService.batchAddDoctorsToService(serviceId, doctorIds);

        if (successCount > 0) {
            response.put("success", true);
            response.put("message", "成功添加 " + successCount + " 位医生");
            response.put("count", successCount);
            return ResponseEntity.ok(response);
        }
        response.put("success", false);
        response.put("message", "添加失败，请检查参数");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
}