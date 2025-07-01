package org.csu.medicine.nursingplatform.service;

import org.csu.medicine.nursingplatform.entity.Complaint;
import org.csu.medicine.nursingplatform.entity.Order;
import org.csu.medicine.nursingplatform.mapper.ComplaintMapper;
import org.csu.medicine.nursingplatform.mapper.OrderMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Service
public class ComplaintService {

    @Autowired
    private ComplaintMapper complaintMapper;

    @Autowired
    private OrderMapper orderMapper;
    //提交投诉
    @Transactional
    public Complaint createComplaint(Complaint complaint) {
        // 验证订单是否存在
        Order order = orderMapper.selectById(complaint.getOrderId());
        if (order == null) {
            throw new RuntimeException("订单不存在");
        }

        // 设置默认状态
        complaint.setStatus(0); // 待处理
        complaint.setCreateTime(LocalDateTime.now());
        complaint.setUpdateTime(LocalDateTime.now());

        complaintMapper.insert(complaint);
        return complaint;
    }
    //通过投诉id获取投诉
    public Complaint getComplaint(Long complaintId) {
        return complaintMapper.selectById(complaintId);
    }
    //获取某用户所有投诉
    public List<Complaint> getUserComplaints(Long userId) {
        return complaintMapper.selectByUserId(userId);
    }
    //获取某订单的投诉
    public List<Complaint> getOrderComplaints(Long orderId) {
        return complaintMapper.selectByOrderId(orderId);
    }
    //反馈投诉
    @Transactional
    public void processComplaint(Long complaintId, Integer status, String feedback) {
        Complaint complaint = complaintMapper.selectById(complaintId);
        if (complaint == null) {
            throw new RuntimeException("投诉不存在");
        }

        complaint.setStatus(status);
        complaint.setFeedback(feedback);
        complaint.setUpdateTime(LocalDateTime.now());
        complaintMapper.updateById(complaint);
    }
}