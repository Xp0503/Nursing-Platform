// MessageConversationMapper.java
package org.csu.medicine.nursingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.csu.medicine.nursingplatform.entity.MessageConversation;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MessageConversationMapper extends BaseMapper<MessageConversation> {

    @Select("SELECT * FROM message_conversation WHERE user_id = #{userId}")
    List<MessageConversation> selectByUserId(@Param("userId") Long userId);
}