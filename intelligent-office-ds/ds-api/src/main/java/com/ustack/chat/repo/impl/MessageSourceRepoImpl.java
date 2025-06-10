package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.chat.entity.MessageSourceEntity;
import com.ustack.chat.repo.MessageSourceRepo;
import com.ustack.chat.mapper.MessageSourceMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author 86187
* @description 针对表【message_source】的数据库操作Service实现
* @createDate 2025-03-04 10:57:32
*/
@Service
public class MessageSourceRepoImpl extends ServiceImpl<MessageSourceMapper, MessageSourceEntity>
    implements MessageSourceRepo {


    @Override
    public int batchInsert(List<MessageSourceEntity> entities) {
        if (entities == null || entities.isEmpty()) {
            return 0;
        }
        return this.getBaseMapper().insertBatchSomeColumn(entities);    }
}




