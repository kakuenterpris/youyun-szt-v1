package com.thtf.chat.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.chat.entity.MessageSourceEntity;
import com.thtf.chat.repo.MessageSourceRepo;
import com.thtf.chat.mapper.MessageSourceMapper;
import org.springframework.stereotype.Service;

/**
* @author 86187
* @description 针对表【message_source】的数据库操作Service实现
* @createDate 2025-03-04 10:57:32
*/
@Service
public class MessageSourceRepoImpl extends ServiceImpl<MessageSourceMapper, MessageSourceEntity>
    implements MessageSourceRepo {

}




