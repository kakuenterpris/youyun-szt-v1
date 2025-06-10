package com.ustack.op.repo.impl;

import cn.hutool.core.collection.CollUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.global.common.utils.Linq;
import com.ustack.op.entity.BusResourceFolderEntity;
import com.ustack.op.mappings.BusResourceFolderMapping;
import com.ustack.resource.dto.BusResourceFolderDTO;
import com.ustack.op.repo.BusResourceFolderRepo;
import com.ustack.op.mapper.BusResourceFolderMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_resource_folder(文件夹表)】的数据库操作Service实现
* @createDate 2025-04-23 11:32:26
*/
@Service
@RequiredArgsConstructor
public class BusResourceFolderRepoImpl extends ServiceImpl<BusResourceFolderMapper, BusResourceFolderEntity>
    implements BusResourceFolderRepo {
    private final BusResourceFolderMapping folderMapping;

    @Override
    public Long add(BusResourceFolderDTO dto) {
        BusResourceFolderEntity entity = folderMapping.dto2Entity(dto);
        entity.setId(null);
        this.save(entity);
        return entity.getId();
    }

    @Override
    public boolean delete(Integer id) {
        return lambdaUpdate()
                .set(BusResourceFolderEntity::getDeleted, true)
                .eq(BusResourceFolderEntity::getId, id)
                .eq(BusResourceFolderEntity::getDeleted, false)
                .update(new BusResourceFolderEntity());
    }

    @Override
    public boolean deleteList(List<Integer> idList) {
        if (CollUtil.isEmpty(idList)) {
            return true;
        }
        return lambdaUpdate()
                .set(BusResourceFolderEntity::getDeleted, true)
                .in(BusResourceFolderEntity::getId, idList)
                .eq(BusResourceFolderEntity::getDeleted, false)
                .update(new BusResourceFolderEntity());
    }

    @Override
    public boolean update(BusResourceFolderDTO dto) {
        BusResourceFolderEntity entity = folderMapping.dto2Entity(dto);

        return lambdaUpdate()
                .eq(BusResourceFolderEntity::getId, dto.getId())
                .eq(BusResourceFolderEntity::getDeleted, false)
                .update(entity);
    }

    @Override
    public List<BusResourceFolderDTO> listAll(boolean notDelete) {
        List<BusResourceFolderEntity> list;
        // notDelete为true时，查询未删除的文件夹，为false时，查询所有文件夹
        if (notDelete) {
            LambdaQueryChainWrapper<BusResourceFolderEntity> wrapper = lambdaQuery();
            wrapper.eq(BusResourceFolderEntity::getDeleted, false);
            list = wrapper.list();
        } else {
            list = this.getBaseMapper().listAllIncludeDeletedFolder();
        }
        return Linq.select(list, folderMapping::entity2Dto);
    }

    @Override
    public List<BusResourceFolderDTO> listAllByType(boolean notDelete, Integer type) {
        List<BusResourceFolderEntity> list;
        // notDelete为true时，查询未删除的文件夹，为false时，查询所有文件夹
        if (notDelete) {
            LambdaQueryChainWrapper<BusResourceFolderEntity> wrapper = lambdaQuery();
            wrapper.eq(BusResourceFolderEntity::getDeleted, false);
            wrapper.in(BusResourceFolderEntity::getType, type, 0);

            list = wrapper.list();
        } else {
            list = this.getBaseMapper().listAllIncludeDeletedFolder();
        }
        return Linq.select(list, folderMapping::entity2Dto);
    }

    @Override
    public List<BusResourceFolderDTO> listOpenView() {
        List<BusResourceFolderEntity> list = lambdaQuery()
                .eq(BusResourceFolderEntity::getOpenView, true)
                .eq(BusResourceFolderEntity::getDeleted, false)
                .list();
        return Linq.select(list, folderMapping::entity2Dto);
    }

    @Override
    public List<BusResourceFolderDTO> listByParentId(Integer parentId, boolean notDelete) {
        List<BusResourceFolderEntity> list;
        // notDelete为true时，查询未删除的文件夹，为false时，查询所有文件夹
        if (notDelete) {
            LambdaQueryChainWrapper<BusResourceFolderEntity> wrapper = lambdaQuery()
                    .eq(BusResourceFolderEntity::getParentId, parentId);
            wrapper.eq(BusResourceFolderEntity::getDeleted, false);
            list = wrapper.list();
        } else {
            list = this.getBaseMapper().listByParentIdIncludeDeletedFolder(parentId);
        }
        return Linq.select(list, folderMapping::entity2Dto);
    }

    @Override
    public BusResourceFolderDTO getOneById(Long id) {
        List<BusResourceFolderEntity> list = lambdaQuery()
                .eq(BusResourceFolderEntity::getId, id)
                .eq(BusResourceFolderEntity::getDeleted, false)
                .list();
        return folderMapping.entity2Dto(Linq.first(list));
    }

    @Override
    public int maxSort() {
        List<BusResourceFolderEntity> list = lambdaQuery()
                .orderByDesc(BusResourceFolderEntity::getSort)
                .list();
        return list.isEmpty() ? 0 : list.get(0).getSort();
    }

    @Override
    public List<BusResourceFolderDTO> listByParentId(Long parentId) {
        List<BusResourceFolderEntity> list = lambdaQuery()
                .eq(BusResourceFolderEntity::getParentId, parentId)
                .eq(BusResourceFolderEntity::getDeleted, false)
                .list();
        return Linq.select(list, folderMapping::entity2Dto);
    }

    @Override
    public boolean updateParent(BusResourceFolderDTO dto) {
        return lambdaUpdate()
                .set(BusResourceFolderEntity::getParentId, dto.getParentId())
                .set(BusResourceFolderEntity::getParentGuid, dto.getParentGuid())
                .eq(BusResourceFolderEntity::getId, dto.getId())
                .eq(BusResourceFolderEntity::getDeleted, false)
                .update(new BusResourceFolderEntity());
    }
}




