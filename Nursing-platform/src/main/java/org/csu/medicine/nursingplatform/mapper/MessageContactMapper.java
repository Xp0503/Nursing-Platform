// MessageContactMapper.java
package org.csu.medicine.nursingplatform.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.csu.medicine.nursingplatform.entity.MessageContact;
import org.springframework.stereotype.Repository;

@Mapper
@Repository
public interface MessageContactMapper extends BaseMapper<MessageContact> {
}
