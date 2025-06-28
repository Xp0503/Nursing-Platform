package org.csu.medicine.nursingplatform.service;

import org.csu.medicine.nursingplatform.entity.SmsCode;
import org.csu.medicine.nursingplatform.entity.User;
import org.csu.medicine.nursingplatform.mapper.SmsCodeMapper;
import org.csu.medicine.nursingplatform.mapper.UserMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.*;
import java.util.regex.Pattern;

@Component
public class UserService {
    @Autowired
    private UserMapper userMapper;
    @Autowired
    private SmsCodeMapper smsCodeMapper;

    /**
     * 发送短信验证码
     *
     * @param phone 手机号
     * @param type  验证码类型(1-注册 2-登录 3-重置密码)
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
        System.out.println("发送短信验证码: " + phone + " - " + code);
    }

    private void validateSmsCode(String phone, String code, Integer type) {
        SmsCode validCode = smsCodeMapper.selectLatestValidCode(
                phone,
                type,
                LocalDateTime.now()
        );
        if(validCode == null) {
            System.out.println("找不到验证码");
        }
        if (validCode == null || !validCode.getCode().equals(code)) {
            throw new RuntimeException("验证码无效或已过期");
        }
    }

    @Transactional
    public void register(User user,String smsCode) {

        // 1. 验证手机号格式
        if (user.getPhone() == null || user.getPhone().isEmpty()) {
            throw new IllegalArgumentException("手机号不能为空");
        }

        // 手机号长度验证
        if (user.getPhone().length() != 11) {
            throw new IllegalArgumentException("手机号必须为11位数字");
        }

        // 手机号数字验证
        if (!user.getPhone().matches("\\d{11}")) {
            throw new IllegalArgumentException("手机号必须为11位数字");
        }

        // 2. 验证身份证号格式
        if (user.getIdnumber() != null && !user.getIdnumber().isEmpty()) {
            String idError = IdNumbervalidate(user.getIdnumber());
            if (idError != null) {
                throw new IllegalArgumentException(idError);
            }
        }

        //3.验证性别格式
        validateGender(user.getGender());
        //4.验证出生日期
        validateBirth(user.getBirth());

        // 验证短信验证码
        validateSmsCode(user.getPhone(),smsCode, 1);

        // 检查手机号是否已注册
        if (userMapper.selectByPhone(user.getPhone()) != null) {
            throw new RuntimeException("手机号已注册");
        }
        // 5. 设置默认值
        setUserDefaults(user);
        // 6. 保存用户
        userMapper.insert(user);

    }

    /**
     * 密码登录
     *
     * @param phone    手机号
     * @param password 密码
     * @return 登录成功的用户对象
     */
    public User login(String phone, String password) {
        User user = userMapper.selectByPhone(phone);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (!password.equals( user.getPassword())) {
            throw new IllegalArgumentException("密码错误");
        }

        if (user.getStatus() == 0) {
            throw new IllegalArgumentException("账号已被禁用");
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        return user;
    }

    /**
     * 短信验证码登录
     *
     * @param phone   手机号
     * @param smsCode 短信验证码
     * @return 登录成功的用户对象
     */
    public User loginWithSms(String phone, String smsCode) {
        // 验证短信验证码
        validateSmsCode(phone, smsCode, 2);

        User user = userMapper.selectByPhone(phone);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (user.getStatus() == 0) {
            throw new IllegalArgumentException("账号已被禁用");
        }

        // 更新最后登录时间
        user.setLastLoginTime(LocalDateTime.now());
        userMapper.updateById(user);

        return user;
    }
    //根据用户名获取用户信息
    public User getUserByName(String userName) {
        Map<String, Object> params = new HashMap<>();
        params.put("userName", userName);
        List<User> users = userMapper.selectByMap(params);
        return users.isEmpty() ? null : users.get(0);
    }


    /**
     * 验证身份证号并返回详细错误信息
     *
     * @param idNumber 身份证号
     * @return 错误信息，如果有效返回null
     */
    public static String IdNumbervalidate(String idNumber) {
        String ID_NUMBER_18_REGEX = "^[1-9]\\d{5}(18|19|20)\\d{2}(0[1-9]|1[0-2])(0[1-9]|[12]\\d|3[01])\\d{3}[0-9Xx]$";
        if (idNumber == null || idNumber.isEmpty()) {
            return "身份证号不能为空";
        }

        int length = idNumber.length();
        if (length != 18) {
            return "身份证号长度应为18位";
        }

        if (length == 18 && !Pattern.matches(ID_NUMBER_18_REGEX, idNumber)) {
            return "18位身份证号格式不正确";
        }


        return null;
    }
    /**
     * 验证性别格式
     */
    private void validateGender(String gender) {
        if (gender == null || gender.isEmpty()) {
            throw new IllegalArgumentException("性别不能为空");
        }
        if (!gender.matches("^(男|女|其他)$")) {
            throw new IllegalArgumentException("性别必须是'男'、'女'或'其他'");
        }
    }

    /**
     * 验证出生日期格式
     */
    private void validateBirth(String birth) {
        if (birth == null || birth.isEmpty()) {
            throw new IllegalArgumentException("出生日期不能为空");
        }

        // 验证日期格式
        if (!birth.matches("^\\d{4}-\\d{2}-\\d{2}$")) {
            throw new IllegalArgumentException("出生日期格式应为YYYY-MM-DD");
        }

        // 验证日期合理性
        String[] parts = birth.split("-");
        int year, month, day;

        try {
            year = Integer.parseInt(parts[0]);
            month = Integer.parseInt(parts[1]);
            day = Integer.parseInt(parts[2]);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("出生日期包含无效数字");
        }

        // 验证年份范围 (1900-当前年份)
        int currentYear = LocalDate.now().getYear();
        if (year < 1900 || year > currentYear) {
            throw new IllegalArgumentException("出生年份应在1900-" + currentYear + "之间");
        }

        // 验证月份
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("月份应在1-12之间");
        }

        // 验证日期
        if (day < 1 || day > 31) {
            throw new IllegalArgumentException("日期应在1-31之间");
        }

        // 验证特定月份的天数
        if (month == 2) {
            // 闰年判断
            boolean isLeapYear = (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
            int maxDay = isLeapYear ? 29 : 28;

            if (day > maxDay) {
                throw new IllegalArgumentException(year + "年2月只有" + maxDay + "天");
            }
        } else if (Arrays.asList(4, 6, 9, 11).contains(month)) {
            if (day > 30) {
                throw new IllegalArgumentException(getMonthName(month) + "只有30天");
            }
        }

        // 验证是否为未来日期
        LocalDate birthDate = LocalDate.parse(birth);
        if (birthDate.isAfter(LocalDate.now())) {
            throw new IllegalArgumentException("出生日期不能是未来日期");
        }

        // 验证年龄合理性
        if (Period.between(birthDate, LocalDate.now()).getYears() > 120) {
            throw new IllegalArgumentException("年龄超过120岁，请确认出生日期");
        }
    }

    /**
     * 获取月份名称
     */
    private String getMonthName(int month) {
        String[] monthNames = {"一月", "二月", "三月", "四月", "五月", "六月",
                "七月", "八月", "九月", "十月", "十一月", "十二月"};
        return monthNames[month - 1];
    }
    private void setUserDefaults(User user) {
        // 设置默认昵称
        if (user.getNickname() == null || user.getNickname().isEmpty()) {
            String suffix = user.getPhone().length() > 4 ?
                    user.getPhone().substring(user.getPhone().length() - 4) :
                    user.getPhone();
            user.setNickname("用户_" + suffix);
        }

        // 设置默认头像
        if (user.getAvatar() == null || user.getAvatar().isEmpty()) {
            user.setAvatar("default_avatar.png");
        }

        // 设置用户状态
        if (user.getStatus() == null) {
            user.setStatus(1); // 正常状态
        }

        // 设置时间戳
        LocalDateTime now = LocalDateTime.now();
        if (user.getCreateTime() == null) {
            user.setCreateTime(now);
        }
        if (user.getUpdateTime() == null) {
            user.setUpdateTime(now);
        }
    }

    /**
     * 通过原密码修改密码
     *
     * @param phone       手机号
     * @param oldPassword 原密码
     * @param newPassword 新密码
     * @param confirmPassword 确认新密码
     */
    @Transactional
    public void changePasswordByOldPassword(String phone, String oldPassword,
                                            String newPassword, String confirmPassword) {
        // 1. 验证密码一致性
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }

        // 2. 验证原密码
        User user = userMapper.selectByPhone(phone);
        if (user == null) {
            throw new IllegalArgumentException("用户不存在");
        }

        if (!oldPassword.equals(user.getPassword())) {
            throw new IllegalArgumentException("原密码错误");
        }

        // 3. 更新密码
        updatePassword(phone, newPassword);
    }
    /**
     * 通过短信验证码修改密码
     *
     * @param phone       手机号
     * @param smsCode     短信验证码
     * @param newPassword 新密码
     * @param confirmPassword 确认新密码
     */
    @Transactional
    public void changePasswordBySmsCode(String phone, String smsCode,
                                        String newPassword, String confirmPassword) {
        // 1. 验证密码一致性
        if (!newPassword.equals(confirmPassword)) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }

        // 2. 验证短信验证码
        validateSmsCode(phone, smsCode, 3); // 类型3:重置密码

        // 3. 更新密码
        updatePassword(phone, newPassword);
    }
    /**
     * 更新密码的核心方法
     */
    private void updatePassword(String phone, String newPassword) {


        // 2. 创建查询条件
        Map<String, Object> params = new HashMap<>();
        params.put("phone", phone);

        // 3. 获取用户
        List<User> users = userMapper.selectByMap(params);
        if (users.isEmpty()) {
            throw new IllegalArgumentException("用户不存在");
        }

        // 4. 更新密码
        User user = users.get(0);
        user.setPassword(newPassword);
        user.setUpdateTime(LocalDateTime.now());

        // 5. 保存更新
        int result = userMapper.updateById(user);
        if (result <= 0) {
            throw new RuntimeException("密码更新失败");
        }

        System.out.println("用户密码已更新: " + phone);
    }

}
