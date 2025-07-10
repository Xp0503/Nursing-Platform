package org.csu.medicine.nursingplatform.service;

import org.csu.medicine.nursingplatform.entity.Doctor;
import org.csu.medicine.nursingplatform.entity.SmsCode;
import org.csu.medicine.nursingplatform.mapper.DoctorMapper;
import org.csu.medicine.nursingplatform.mapper.SmsCodeMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

@Service
public class DoctorService {

    @Autowired
    private DoctorMapper doctorMapper;

    @Autowired
    private SmsCodeMapper smsCodeMapper;

    /**
     * 发送短信验证码
     */
    @Transactional
    public void sendSmsCode(String phone, Integer type) {
        if (!phone.matches("^1[3-9]\\d{9}$")) {
            throw new IllegalArgumentException("手机号格式不正确");
        }

        // 生成6位随机验证码
        String code = String.format("%06d", new Random().nextInt(999999));

        // 保存验证码到数据库
        SmsCode smsCode = new SmsCode();
        smsCode.setPhone(phone);
        smsCode.setCode(code);
        smsCode.setType(type);
        smsCode.setExpireTime(LocalDateTime.now().plusMinutes(5));
        smsCodeMapper.insert(smsCode);

        // TODO: 实际项目中调用短信服务发送验证码
        System.out.println("发送医生短信验证码: " + phone + " - " + code);
    }

    /**
     * 验证短信验证码
     */
    private void validateSmsCode(String phone, String code, Integer type) {
        SmsCode validCode = smsCodeMapper.selectLatestValidCode(
                phone, type, LocalDateTime.now());

        if (validCode == null || !validCode.getCode().equals(code)) {
            throw new RuntimeException("验证码无效或已过期");
        }
    }

    /**
     * 医生注册
     */
    @Transactional
    public void register(Doctor doctor, String smsCode) {
        // 验证短信验证码
        validateSmsCode(doctor.getPhone(), smsCode, 1);

        // 检查手机号是否已注册
        if (doctorMapper.selectByPhone(doctor.getPhone()) != null) {
            throw new RuntimeException("手机号已注册");
        }

        // 设置默认值
        setDoctorDefaults(doctor);

        // 保存医生信息
        doctorMapper.insert(doctor);
    }

    /**
     * 密码登录
     */
    public Doctor login(String phone, String password) {
        Doctor doctor = doctorMapper.selectByPhone(phone);

        if (doctor == null) {
            throw new IllegalArgumentException("医生不存在");
        }

        if (!password.equals(doctor.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }

        // 更新最后登录时间
        doctor.setLastLoginTime(LocalDateTime.now());
        doctorMapper.updateById(doctor);

        return doctor;
    }

    /**
     * 短信验证码登录
     */
    public Doctor loginWithSms(String phone, String smsCode) {
        validateSmsCode(phone, smsCode, 2);

        Doctor doctor = doctorMapper.selectByPhone(phone);
        if (doctor == null) {
            throw new IllegalArgumentException("医生不存在");
        }

        // 更新最后登录时间
        doctor.setLastLoginTime(LocalDateTime.now());
        doctorMapper.updateById(doctor);

        return doctor;
    }

    /**
     * 设置医生默认值
     */
    private void setDoctorDefaults(Doctor doctor) {
        // 设置默认头像
        if (doctor.getAvatar() == null || doctor.getAvatar().isEmpty()) {
            doctor.setAvatar("default_avatar.png");
        }

        // 设置时间戳
        LocalDateTime now = LocalDateTime.now();
        if (doctor.getCreateTime() == null) {
            doctor.setCreateTime(now);
        }
        if (doctor.getUpdateTime() == null) {
            doctor.setUpdateTime(now);
        }
    }

    /**
     * 修改密码（通过原密码）
     */
    @Transactional
    public void changePasswordByOldPassword(String phone, String oldPassword,
                                            String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }

        Doctor doctor = doctorMapper.selectByPhone(phone);
        if (doctor == null) {
            throw new IllegalArgumentException("医生不存在");
        }

        if (!oldPassword.equals(doctor.getPassword())) {
            throw new IllegalArgumentException("原密码错误");
        }

        updatePassword(phone, newPassword);
    }

    /**
     * 修改密码（通过短信验证码）
     */
    @Transactional
    public void changePasswordBySmsCode(String phone, String smsCode,
                                        String newPassword, String confirmPassword) {
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }

        validateSmsCode(phone, smsCode, 3); // 类型3:重置密码
        updatePassword(phone, newPassword);
    }

    /**
     * 更新密码核心方法
     */
    private void updatePassword(String phone, String newPassword) {
        Doctor doctor = doctorMapper.selectByPhone(phone);
        if (doctor == null) {
            throw new IllegalArgumentException("医生不存在");
        }

        doctor.setPassword(newPassword);
        doctor.setUpdateTime(LocalDateTime.now());

        int result = doctorMapper.updateById(doctor);
        if (result <= 0) {
            throw new RuntimeException("密码更新失败");
        }
    }

    /**
     * 更新医生信息
     */
    @Transactional
    public void updateDoctor(Doctor doctor) {
        if (doctor.getPhone() == null || doctor.getPhone().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }

        // 验证性别格式
        validateGender(doctor.getGender());

        // 设置更新时间
        doctor.setUpdateTime(LocalDateTime.now());

        int result = doctorMapper.updateById(doctor);
        if (result <= 0) {
            throw new RuntimeException("医生信息更新失败");
        }
    }

    /**
     * 验证性别格式
     */
    private void validateGender(String gender) {
        if (gender == null || gender.isEmpty()) {
            throw new IllegalArgumentException("性别不能为空");
        }
        if (!gender.matches("^(男|女)$")) {
            throw new IllegalArgumentException("性别必须是'男'或'女'");
        }
    }
}
