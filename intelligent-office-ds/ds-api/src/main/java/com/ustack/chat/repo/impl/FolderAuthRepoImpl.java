package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.chat.entity.FolderAuthEntity;
import com.ustack.chat.mapper.FolderAuthMapper;
import com.ustack.chat.repo.FolderAuthRepo;
import org.springframework.stereotype.Service;

@Service
public class FolderAuthRepoImpl extends ServiceImpl<FolderAuthMapper, FolderAuthEntity> implements FolderAuthRepo {

}
