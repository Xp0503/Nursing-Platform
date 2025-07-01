package org.csu.medicine.nursingplatform.entity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
@Data
@TableName("healthcare_service")
public class HealthcareService {
        @TableId(type = IdType.AUTO)
        private Long id;          // 服务ID

        private String name;       // 服务名称
        private Integer categoryId; // 分类ID
        private BigDecimal price;  // 服务价格

        @TableField("`duration`") // 使用反引号转义关键字
        private Integer duration; // 预计时长(分钟)

        private String description;    // 服务描述
        private String suitableCrowd; // 适用人群

        private Integer status;     // 状态(0-下架 1-上架)

        @TableField(fill = FieldFill.INSERT)
        private LocalDateTime createTime; // 创建时间

        private String imageUrl;


        @Override
        public String toString() {
                return "HealthcareService{" +
                        "id=" + id +
                        ", name='" + name + '\'' +
                        ", categoryId=" + categoryId +
                        ", price=" + price +
                        ", duration=" + duration +
                        ", description='" + description + '\'' +
                        ", suitableCrowd='" + suitableCrowd + '\'' +
                        ", status=" + status +
                        ", createTime=" + createTime +
                        ", imageUrl='" + imageUrl + '\'' +
                        '}';
        }

        public Long getId() {
                return id;
        }

        public void setId(Long id) {
                this.id = id;
        }

        public String getName() {
                return name;
        }

        public void setName(String name) {
                this.name = name;
        }

        public Integer getCategoryId() {
                return categoryId;
        }

        public void setCategoryId(Integer categoryId) {
                this.categoryId = categoryId;
        }

        public BigDecimal getPrice() {
                return price;
        }

        public void setPrice(BigDecimal price) {
                this.price = price;
        }

        public Integer getDuration() {
                return duration;
        }

        public void setDuration(Integer duration) {
                this.duration = duration;
        }

        public String getDescription() {
                return description;
        }

        public void setDescription(String description) {
                this.description = description;
        }

        public String getSuitableCrowd() {
                return suitableCrowd;
        }

        public void setSuitableCrowd(String suitableCrowd) {
                this.suitableCrowd = suitableCrowd;
        }

        public Integer getStatus() {
                return status;
        }

        public void setStatus(Integer status) {
                this.status = status;
        }

        public LocalDateTime getCreateTime() {
                return createTime;
        }

        public void setCreateTime(LocalDateTime createTime) {
                this.createTime = createTime;
        }

        public String getImageUrl() {
                return imageUrl;
        }

        public void setImageUrl(String imageUrl) {
                this.imageUrl = imageUrl;
        }
}
