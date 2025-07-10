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
    @Transactional
    public Complaint createComplaint(Complaint complaint) {
        Order order = orderMapper.selectById(complaint.getOrderId());
        if (order == null) throw new RuntimeException("订单不存在");

        // 从订单获取关联医生ID
        if(order.getDoctorId() != null) {
            complaint.setDoctorId(order.getDoctorId());
        }

        complaint.setStatus(0);
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
    public void processComplaint(Long complaintId, Integer status, String feedback, Long handlerDoctorId) {
        Complaint complaint = complaintMapper.selectById(complaintId);
        if (complaint == null) throw new RuntimeException("投诉不存在");

        complaint.setStatus(status);
        complaint.setFeedback(feedback);
        complaint.setHandlerDoctorId(handlerDoctorId); // 设置处理医生
        complaint.setUpdateTime(LocalDateTime.now());
        complaintMapper.updateById(complaint);
    }

    public List<Complaint> getComplaintsByDoctor(Long doctorId) {
        return complaintMapper.selectByDoctorId(doctorId);
    }

    public List<Complaint> getComplaintsByHandler(Long doctorId) {
        return complaintMapper.selectByHandlerDoctorId(doctorId);
    }
}