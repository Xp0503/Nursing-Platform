package org.csu.medicine.nursingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.csu.medicine.nursingplatform.entity.User;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

@Component
@Repository()
@Mapper
public interface UserMapper extends BaseMapper<User> {
    @Select("SELECT * FROM user WHERE phone = #{phone}")
    User selectByPhone(@Param("phone") String phone);
}
