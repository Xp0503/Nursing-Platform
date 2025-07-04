package org.csu.medicine.nursingplatform.controller;

import org.csu.medicine.nursingplatform.entity.OrderEvaluation;
import org.csu.medicine.nursingplatform.service.EvaluationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/evaluations")
@CrossOrigin
public class EvaluationController {

    @Autowired
    private EvaluationService evaluationService;

    // 提交评价
    @PostMapping
    public ResponseEntity<OrderEvaluation> createEvaluation(
            @RequestBody OrderEvaluation evaluation) {
        return ResponseEntity.ok(evaluationService.createEvaluation(evaluation));
    }

    // 获取订单评价
    @GetMapping("/order/{orderId}")
    public ResponseEntity<OrderEvaluation> getEvaluationByOrder(
            @PathVariable Long orderId) {
        return ResponseEntity.ok(evaluationService.getEvaluationByOrder(orderId));
    }

    // 获取用户评价列表
    @GetMapping("/user/{userId}")
    public ResponseEntity<List<OrderEvaluation>> getUserEvaluations(
            @PathVariable Long userId) {
        return ResponseEntity.ok(evaluationService.getUserEvaluations(userId));
    }

    // 获取服务评价列表（通过订单关联）
    @GetMapping("/service/{serviceId}")
    public ResponseEntity<List<OrderEvaluation>> getServiceEvaluations(
            @PathVariable Long serviceId) {
        return ResponseEntity.ok(evaluationService.getServiceEvaluations(serviceId));
    }

    // 删除评价（软删除）
    @DeleteMapping("/{evaluationId}")
    public ResponseEntity<Void> deleteEvaluation(
            @PathVariable Long evaluationId) {
        evaluationService.deleteEvaluation(evaluationId);
        return ResponseEntity.ok().build();
    }
}
