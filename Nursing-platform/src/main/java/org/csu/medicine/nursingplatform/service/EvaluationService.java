package org.csu.medicine.nursingplatform.service;

import org.csu.medicine.nursingplatform.entity.Order;
import org.csu.medicine.nursingplatform.entity.OrderEvaluation;
import org.csu.medicine.nursingplatform.mapper.OrderEvaluationMapper;
import org.csu.medicine.nursingplatform.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

@Service
public class EvaluationService {

    @Autowired
    private OrderEvaluationMapper evaluationMapper;

    @Autowired
    private OrderMapper orderMapper;

    //创建评价
    @Transactional
    public OrderEvaluation createEvaluation(OrderEvaluation evaluation) {
        // 验证订单是否已完成
        Order order = orderMapper.selectById(evaluation.getOrderId());
        if (order == null || order.getStatus() != 3) {
            throw new RuntimeException("订单不存在或未完成");
        }

        // 验证是否已评价
        OrderEvaluation existing = evaluationMapper.selectByOrderId(evaluation.getOrderId());
        if (existing != null) {
            throw new RuntimeException("该订单已评价");
        }

        // 创建评价
        evaluation.setCreateTime(new Date());
        evaluation.setIsDeleted(0);
        evaluationMapper.insert(evaluation);
        return evaluation;
    }

    public OrderEvaluation getEvaluationByOrder(Long orderId) {
        return evaluationMapper.selectByOrderId(orderId);
    }

    public List<OrderEvaluation> getUserEvaluations(Long userId) {
        return evaluationMapper.selectByUserId(userId);
    }

    public List<OrderEvaluation> getServiceEvaluations(Long serviceId) {
        return evaluationMapper.selectByServiceId(serviceId);
    }

    @Transactional
    public void deleteEvaluation(Long evaluationId) {
        OrderEvaluation evaluation = evaluationMapper.selectById(evaluationId);
        if (evaluation == null) {
            throw new RuntimeException("评价不存在");
        }
        evaluation.setIsDeleted(1);
        evaluationMapper.updateById(evaluation);
    }
}