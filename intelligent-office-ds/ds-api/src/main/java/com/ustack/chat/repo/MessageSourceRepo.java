package com.ustack.chat.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.chat.entity.MessageSourceEntity;

import java.util.List;

/**
* @author 86187
* @description 针对表【message_source】的数据库操作Service
* @createDate 2025-03-04 10:57:32
*/
public interface MessageSourceRepo extends IService<MessageSourceEntity> {

    int batchInsert(List<MessageSourceEntity> entities);
}
