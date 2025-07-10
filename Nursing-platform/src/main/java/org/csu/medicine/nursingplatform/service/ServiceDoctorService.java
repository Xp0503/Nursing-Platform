package org.csu.medicine.nursingplatform.service;

import org.csu.medicine.nursingplatform.entity.ServiceDoctor;
import org.csu.medicine.nursingplatform.mapper.ServiceDoctorMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ServiceDoctorService {

    private final ServiceDoctorMapper serviceDoctorMapper;

    @Autowired
    public ServiceDoctorService(ServiceDoctorMapper serviceDoctorMapper) {
        this.serviceDoctorMapper = serviceDoctorMapper;
    }
    // 添加以下方法
    public boolean existsByServiceAndDoctor(Long serviceId, Long doctorId) {
        return serviceDoctorMapper.existsByServiceAndDoctor(serviceId, doctorId);
    }
    // 添加服务-医生关联
    @Transactional
    public boolean addAssociation(Long serviceId, Long doctorId) {
        if (serviceDoctorMapper.existsByServiceAndDoctor(serviceId, doctorId)) {
            return false;
        }
        return serviceDoctorMapper.insertAssociation(serviceId, doctorId) == 1;
    }

    // 删除特定关联
    @Transactional
    public boolean removeAssociation(Long serviceId, Long doctorId) {
        return serviceDoctorMapper.deleteAssociation(serviceId, doctorId) == 1;
    }

    // 根据服务ID删除所有关联
    @Transactional
    public boolean removeAssociationsByService(Long serviceId) {
        return serviceDoctorMapper.deleteByServiceId(serviceId) > 0;
    }

    // 根据医生ID删除所有关联
    @Transactional
    public boolean removeAssociationsByDoctor(Long doctorId) {
        return serviceDoctorMapper.deleteByDoctorId(doctorId) > 0;
    }

    // 获取服务的医生列表
    public List<Long> getDoctorsByService(Long serviceId) {
        return serviceDoctorMapper.findDoctorIdsByServiceId(serviceId);
    }

    // 获取医生的服务列表
    public List<Long> getServicesByDoctor(Long doctorId) {
        return serviceDoctorMapper.findServiceIdsByDoctorId(doctorId);
    }

    // 批量添加医生到服务
    @Transactional
    public int batchAddDoctorsToService(Long serviceId, List<Long> doctorIds) {
        int successCount = 0;
        for (Long doctorId : doctorIds) {
            if (addAssociation(serviceId, doctorId)) {
                successCount++;
            }
        }
        return successCount;
    }
}