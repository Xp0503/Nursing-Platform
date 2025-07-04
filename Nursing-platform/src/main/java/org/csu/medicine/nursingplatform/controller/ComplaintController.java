package org.csu.medicine.nursingplatform.controller;

import org.csu.medicine.nursingplatform.entity.Complaint;
import org.csu.medicine.nursingplatform.service.ComplaintService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/complaints")
@CrossOrigin
public class ComplaintController {

    @Autowired
    private ComplaintService complaintService;

    // 提交投诉
    @PostMapping
    public ResponseEntity<Complaint> createComplaint(
            @RequestBody Complaint complaint) {
        System.out.println(complaint);
        return ResponseEntity.ok(complaintService.createComplaint(complaint));
    }

    // 获取投诉详情
    @GetMapping("/{complaintId}")
    public ResponseEntity<Complaint> getComplaint(
            @PathVariable Long complaintId) {
        return ResponseEntity.ok(complaintService.getComplaint(complaintId));
    }

    // 获取用户投诉列表
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<Complaint>> getUserComplaints(
            @PathVariable Long userId) {
        return ResponseEntity.ok(complaintService.getUserComplaints(userId));
    }

    // 获取订单相关投诉
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<Complaint>> getOrderComplaints(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(complaintService.getOrderComplaints(orderId));
    }

    // 处理投诉
    @PutMapping("/{complaintId}/process")
    public ResponseEntity<Void> processComplaint(
            @PathVariable Long complaintId,
            @RequestParam Integer status,
            @RequestParam String feedback) {
        complaintService.processComplaint(complaintId, status, feedback);
        return ResponseEntity.ok().build();
    }
}
