package org.csu.medicine.nursingplatform.controller;

import org.csu.medicine.nursingplatform.entity.Doctor;
import org.csu.medicine.nursingplatform.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doctors")
@CrossOrigin
public class DoctorController {

    @Autowired
    private DoctorService doctorService;

    /**
     * 发送短信验证码
     */
    @PostMapping("/send-sms")
    public ResponseEntity<?> sendSmsCode(
            @RequestParam String phone,
            @RequestParam Integer type) {
        try {
            doctorService.sendSmsCode(phone, type);
            return ResponseEntity.ok().body("短信验证码发送成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("服务器错误: " + e.getMessage());
        }
    }

    /**
     * 医生注册
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody Doctor doctor,
            @RequestParam String smsCode) {
        try {
            doctorService.register(doctor, smsCode);
            return ResponseEntity.ok(doctor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("服务器错误: " + e.getMessage());
        }
    }

    /**
     * 密码登录
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam String phone,
            @RequestParam String password) {
        try {
            Doctor doctor = doctorService.login(phone, password);
            return ResponseEntity.ok(doctor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("服务器错误: " + e.getMessage());
        }
    }

    /**
     * 短信验证码登录
     */
    @PostMapping("/login/sms")
    public ResponseEntity<?> loginWithSms(
            @RequestParam String phone,
            @RequestParam String smsCode) {
        try {
            Doctor doctor = doctorService.loginWithSms(phone, smsCode);
            return ResponseEntity.ok(doctor);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("服务器错误: " + e.getMessage());
        }
    }

    /**
     * 通过原密码修改密码
     */
    @PostMapping("/change-password/by-password")
    public ResponseEntity<?> changePasswordByOldPassword(
            @RequestParam String phone,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {
        try {
            doctorService.changePasswordByOldPassword(phone, oldPassword, newPassword, confirmPassword);
            return ResponseEntity.ok().body("密码修改成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("服务器错误: " + e.getMessage());
        }
    }

    /**
     * 通过短信验证码修改密码
     */
    @PostMapping("/change-password/by-sms")
    public ResponseEntity<?> changePasswordBySmsCode(
            @RequestParam String phone,
            @RequestParam String smsCode,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {
        try {
            doctorService.changePasswordBySmsCode(phone, smsCode, newPassword, confirmPassword);
            return ResponseEntity.ok().body("密码修改成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("服务器错误: " + e.getMessage());
        }
    }

    /**
     * 更新医生信息
     */
    @PutMapping("/update")
    public ResponseEntity<?> updateDoctor(@RequestBody Doctor doctor) {
        try {
            doctorService.updateDoctor(doctor);
            return ResponseEntity.ok("医生信息更新成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("服务器错误: " + e.getMessage());
        }
    }
}