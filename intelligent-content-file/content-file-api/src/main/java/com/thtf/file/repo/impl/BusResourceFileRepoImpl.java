package com.thtf.file.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.file.entity.BusResourceFileEntity;
import com.thtf.file.mapper.BusResourceFileMapper;
import com.thtf.file.repo.BusResourceFileRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

/**
* @author allm
* @description 针对表【bus_resource_file(文件表)】的数据库操作Service实现
* @createDate 2025-04-23 11:43:03
*/
@Service
@RequiredArgsConstructor
public class BusResourceFileRepoImpl extends ServiceImpl<BusResourceFileMapper, BusResourceFileEntity>
    implements BusResourceFileRepo {

}




