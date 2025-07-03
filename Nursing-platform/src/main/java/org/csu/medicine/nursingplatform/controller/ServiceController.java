package org.csu.medicine.nursingplatform.controller;

import org.csu.medicine.nursingplatform.entity.HealthcareService;
import org.csu.medicine.nursingplatform.service.ServiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/services")
@CrossOrigin
public class ServiceController {
    @Autowired
    private ServiceService serviceService;

    // 获取所有上架服务
    @GetMapping
    public ResponseEntity<List<HealthcareService>> getAllActiveServices() {
        return ResponseEntity.ok(serviceService.getActiveServices());
    }

    // 按分类获取上架服务
    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<HealthcareService>> getServicesByCategory(
            @PathVariable Integer categoryId) {
        return ResponseEntity.ok(serviceService.getActiveServicesByCategory(categoryId));
    }


    // 获取服务详情
    @GetMapping("/{id}")
    public ResponseEntity<HealthcareService> getServiceDetails(@PathVariable Long id) {
        return ResponseEntity.ok(serviceService.getServiceDetails(id));
    }

    // 搜索服务
    @GetMapping("/search")
    public ResponseEntity<List<HealthcareService>> searchServices(
            @RequestParam String keyword) {
        return ResponseEntity.ok(serviceService.searchServices(keyword));
    }

    // 获取热门服务
    @GetMapping("/popular")
    public ResponseEntity<List<HealthcareService>> getPopularServices() {
        return ResponseEntity.ok(serviceService.getPopularServices());
    }

    // 获取推荐服务
    @GetMapping("/recommended")
    public ResponseEntity<List<HealthcareService>> getRecommendedServices() {
        return ResponseEntity.ok(serviceService.getRecommendedServices());
    }
}
