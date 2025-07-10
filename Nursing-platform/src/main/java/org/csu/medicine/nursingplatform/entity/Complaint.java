package org.csu.medicine.nursingplatform.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("complaint")
public class Complaint {
    @TableId(type = IdType.AUTO)
    private Long id;
    private Long userId;
    private Long orderId;
    private Integer type;
    private String content;
    private String imageUrls;
    private Integer status;
    private String feedback;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long doctorId;         // 新增: 被投诉医生ID
    private Long handlerDoctorId;  // 新增: 处理医生ID
    @Override
    public String toString() {
        return "Complaint{" +
                "id=" + id +
                ", userId=" + userId +
                ", orderId=" + orderId +
                ", type=" + type +
                ", content='" + content + '\'' +
                ", imageUrls='" + imageUrls + '\'' +
                ", status=" + status +
                ", feedback='" + feedback + '\'' +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                ", doctorId=" + doctorId +          // 新增
                ", handlerDoctorId=" + handlerDoctorId + // 新增
                '}';
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Integer getType() {
        return type;
    }

    public void setType(Integer type) {
        this.type = type;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageUrls() {
        return imageUrls;
    }

    public void setImageUrls(String imageUrls) {
        this.imageUrls = imageUrls;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }

    public LocalDateTime getCreateTime() {
        return createTime;
    }

    public void setCreateTime(LocalDateTime createTime) {
        this.createTime = createTime;
    }

    public LocalDateTime getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(LocalDateTime updateTime) {
        this.updateTime = updateTime;
    }
    public Long getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(Long doctorId) {
        this.doctorId = doctorId;
    }

    public Long getHandlerDoctorId() {
        return handlerDoctorId;
    }

    public void setHandlerDoctorId(Long handlerDoctorId) {
        this.handlerDoctorId = handlerDoctorId;
    }


}