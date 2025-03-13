package com.thtf.chat.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.thtf.chat.entity.TableTestEntity;
import com.thtf.chat.mapper.TableTestMapper;
import com.thtf.chat.repo.TableTestRepo;
import org.springframework.stereotype.Service;

/**
* @author cnkittod
* @description 针对表【table_test】的数据库操作Service实现
* @createDate 2025-02-18 00:14:30
*/
@Service
public class TableTestServiceImpl extends ServiceImpl<TableTestMapper, TableTestEntity>
    implements TableTestRepo {


}




