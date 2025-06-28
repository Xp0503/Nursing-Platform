package org.csu.medicine.nursingplatform.controller;

import org.csu.medicine.nursingplatform.entity.User;
import org.csu.medicine.nursingplatform.mapper.UserMapper;
import org.csu.medicine.nursingplatform.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@CrossOrigin
public class UserController {

    @Autowired
    private UserService userService;
    @Qualifier("userMapper")
    @Autowired
    private UserMapper userMapper;

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param type  验证码类型(1-注册 2-登录 3-重置密码)
     * @return 操作结果
     */
    @PostMapping("/send-sms")
    public ResponseEntity<?> sendSmsCode(
            @RequestParam String phone,
            @RequestParam Integer type) {
        try {
            userService.sendSmsCode(phone, type);
            return ResponseEntity.ok().body("短信验证码发送成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("服务器错误: " + e.getMessage());
        }
    }

    /**
     * 用户注册
     *
     * @param user    用户信息
     * @param smsCode 短信验证码
     * @return 注册成功的用户信息
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(
            @RequestBody User user,
            @RequestParam String smsCode) {
        try {
            userService.register(user, smsCode);
            return ResponseEntity.ok(user);
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
     *
     * @param phone    手机号
     * @param password 密码
     * @return 登录成功的用户信息
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(
            @RequestParam String phone,
            @RequestParam String password) {
        try {
            User user = userService.login(phone, password);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("服务器错误: " + e.getMessage());
        }
    }

    /**
     * 短信验证码登录
     *
     * @param phone   手机号
     * @param smsCode 短信验证码
     * @return 登录成功的用户信息
     */
    @PostMapping("/login/sms")
    public ResponseEntity<?> loginWithSms(
            @RequestParam String phone,
            @RequestParam String smsCode) {
        try {
            User user = userService.loginWithSms(phone, smsCode);
            return ResponseEntity.ok(user);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("服务器错误: " + e.getMessage());
        }
    }

    /**
     * 根据用户名获取用户信息
     *
     * @param userName 用户名
     * @return 用户信息
     */
    @GetMapping("/user")
    public ResponseEntity<?> getUserByName(
            @RequestParam String userName) {
        try {
            User user = userService.getUserByName(userName);
            if (user == null) {
                return ResponseEntity.notFound().build();
            }
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("服务器错误: " + e.getMessage());
        }
    }
    /**
     * 通过原密码修改密码
     *
     * @param phone 手机号
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @param confirmPassword 确认新密码
     * @return 操作结果
     */
    @PostMapping("/change-password/by-password")
    public ResponseEntity<?> changePasswordByOldPassword(
            @RequestParam String phone,
            @RequestParam String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {
        try {
            userService.changePasswordByOldPassword(phone, oldPassword, newPassword, confirmPassword);
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
     *
     * @param phone 手机号
     * @param smsCode 短信验证码
     * @param newPassword 新密码
     * @param confirmPassword 确认新密码
     * @return 操作结果
     */
    @PostMapping("/change-password/by-sms")
    public ResponseEntity<?> changePasswordBySmsCode(
            @RequestParam String phone,
            @RequestParam String smsCode,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword) {
        try {
            userService.changePasswordBySmsCode(phone, smsCode, newPassword, confirmPassword);
            return ResponseEntity.ok().body("密码修改成功");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("服务器错误: " + e.getMessage());
        }
    }
}
