package org.csu.medicine.nursingplatform.service;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.csu.medicine.nursingplatform.entity.HealthcareService;
import org.csu.medicine.nursingplatform.mapper.ServiceMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;

@Component
@Service
public class ServiceService {
    @Autowired
    private ServiceMapper serviceMapper;


    /**
     * 获取所有上架服务
     */
    public List<HealthcareService> getActiveServices() {
        return serviceMapper.selectActiveServices();
    }

    /**
     * 按分类获取上架服务
     */
    public List<HealthcareService> getActiveServicesByCategory(Integer categoryId) {
        if (categoryId == null || categoryId <= 0) {
            throw new IllegalArgumentException("分类ID无效");
        }
        return serviceMapper.selectActiveByCategoryId(categoryId);
    }

    /**
     * 根据ID获取服务详情
     * @param id 服务ID
     */
    public HealthcareService getServiceDetails(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("服务ID无效");
        }

        HealthcareService service = serviceMapper.selectById(id);
        if (service == null) {
            throw new RuntimeException("服务不存在或已下架");
        }
        return service;
    }




    /**
     * 更新服务状态
     * @param id 服务ID
     * @param status 新状态 (0-下架, 1-上架)
     */
    @Transactional
    public void updateStatus(Long id, Integer status) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("服务ID无效");
        }

        if (status == null || (status != 0 && status != 1)) {
            throw new IllegalArgumentException("状态值无效");
        }

        HealthcareService service = new HealthcareService();
        service.setId(id);
        service.setStatus(status);

        if (serviceMapper.updateById(service) <= 0) {
            throw new RuntimeException("更新状态失败");
        }
    }

    /**
     * 搜索服务
     * @param keyword 搜索关键词
     */
    public List<HealthcareService> searchServices(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            throw new IllegalArgumentException("搜索关键词不能为空");
        }
        return serviceMapper.searchServices("%" + keyword + "%");
    }

    /**
     * 获取热门服务（示例：按创建时间倒序取前10个）
     */
    public List<HealthcareService> getPopularServices() {
        return serviceMapper.selectList(
                new QueryWrapper<HealthcareService>()
                        .eq("status", 1)
                        .orderByDesc("create_time")
                        .last("LIMIT 10")
        );
    }

    /**
     * 获取推荐服务（示例：固定分类）
     */
    public List<HealthcareService> getRecommendedServices() {
        return serviceMapper.selectActiveByCategoryId(1); // 假设分类1是推荐分类
    }


}
