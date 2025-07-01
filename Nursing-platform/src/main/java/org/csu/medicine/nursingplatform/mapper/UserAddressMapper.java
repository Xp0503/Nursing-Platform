package org.csu.medicine.nursingplatform.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import org.apache.ibatis.annotations.*;
import org.csu.medicine.nursingplatform.entity.UserAddress;

@Mapper
public interface UserAddressMapper extends BaseMapper<UserAddress> {

    @Update("UPDATE user_address SET is_default = 0 WHERE user_id = #{userId} AND is_default = 1")
    int clearDefaultAddress(@Param("userId") Long userId);

    @Select("SELECT * FROM user_address WHERE user_id = #{userId} AND is_default = 1")
    UserAddress findDefaultAddressByUserId(@Param("userId") Long userId);
}