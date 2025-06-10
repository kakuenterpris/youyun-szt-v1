package com.ustack.chat.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ustack.chat.entity.TableTestEntity;
import com.ustack.chat.mapper.TableTestMapper;
import com.ustack.chat.repo.TableTestRepo;
import org.springframework.stereotype.Service;

/**
* @description 针对表【table_test】的数据库操作Service实现
* @createDate 2025-02-18 00:14:30
*/
@Service
public class TableTestServiceImpl extends ServiceImpl<TableTestMapper, TableTestEntity>
    implements TableTestRepo {

}
