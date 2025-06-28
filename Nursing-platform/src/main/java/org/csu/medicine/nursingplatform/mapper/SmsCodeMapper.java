package org.csu.medicine.nursingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.csu.medicine.nursingplatform.entity.SmsCode;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Component
@Repository()
@Mapper
public interface SmsCodeMapper extends BaseMapper<SmsCode> {
    @Select("SELECT * FROM sms_code WHERE phone = #{phone} AND type = #{type} AND expire_time > #{now} ORDER BY create_time DESC LIMIT 1")
    SmsCode selectLatestValidCode(
            @Param("phone") String phone,
            @Param("type") Integer type,
            @Param("now") LocalDateTime now
    );
}
