package com.ustack.chat.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.ustack.chat.entity.TableTestEntity;
import com.ustack.chat.mappings.ExampleMapping;
import com.ustack.chat.repo.TableTestRepo;
import com.ustack.chat.service.TestService;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.utils.Linq;
import com.ustack.test.BizErrorCode;
import com.ustack.test.TableTestBO;
import com.ustack.test.TableTestDTO;
import com.ustack.test.TableTestVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: TestService
 * @Date: 2025-02-17 23:57
 */
@Service
@RequiredArgsConstructor
public class TestServiceImpl implements TestService {

    private final TableTestRepo repo;
    private final ExampleMapping mapping;


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse testInsert(TableTestDTO param) {
        // 其他校验
        TableTestBO bo = mapping.dto2Bo(param);
        // bo处理业务逻辑

        // 保存
        TableTestEntity entity = mapping.bo2Entity(bo);
        boolean save = repo.save(entity);

        return save ? RestResponse.success(entity) : RestResponse.fail(BizErrorCode.attendance_item_create_failed);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse testUpdate(TableTestDTO param) {

        // 其他校验
        TableTestBO bo = mapping.dto2Bo(param);
        // bo处理业务逻辑

        // 保存
        TableTestEntity entity = mapping.bo2Entity(bo);

        boolean save = repo.updateById(entity);

        return save ? RestResponse.success(entity) : RestResponse.fail(BizErrorCode.attendance_item_create_failed);

    }

    @Override
    public RestResponse testQuery(TableTestDTO param){
        List<TableTestBO> list = Linq.select(repo.list(new LambdaQueryWrapper<>()), mapping::entity2Bo);
        return RestResponse.success(list);
    }
}
