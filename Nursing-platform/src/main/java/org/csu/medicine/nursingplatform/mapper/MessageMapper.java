// MessageMapper.java
package org.csu.medicine.nursingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.csu.medicine.nursingplatform.entity.Message;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface MessageMapper extends BaseMapper<Message> {

    @Select("SELECT * FROM message WHERE conversation_id = #{conversationId} ORDER BY create_time ASC")
    List<Message> selectByConversationId(@Param("conversationId") Long conversationId);
}