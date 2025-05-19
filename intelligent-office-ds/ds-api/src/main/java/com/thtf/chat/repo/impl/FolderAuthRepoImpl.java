package com.thtf.chat.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.chat.entity.FolderAuthEntity;
import com.thtf.chat.mapper.FolderAuthMapper;
import com.thtf.chat.repo.FolderAuthRepo;
import org.springframework.stereotype.Service;

@Service
public class FolderAuthRepoImpl extends ServiceImpl<FolderAuthMapper, FolderAuthEntity> implements FolderAuthRepo {

}
