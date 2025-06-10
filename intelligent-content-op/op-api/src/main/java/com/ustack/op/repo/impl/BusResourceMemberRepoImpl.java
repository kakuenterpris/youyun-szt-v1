package com.ustack.op.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.global.common.utils.Linq;
import com.ustack.op.entity.BusResourceMemberEntity;
import com.ustack.op.mappings.BusResourceMemberMapping;
import com.ustack.resource.dto.BusResourceMemberDTO;
import com.ustack.op.repo.BusResourceMemberRepo;
import com.ustack.op.mapper.BusResourceMemberMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_resource_member(文件夹成员权限表)】的数据库操作Service实现
* @createDate 2025-04-23 11:43:16
*/
@Service
@RequiredArgsConstructor
public class BusResourceMemberRepoImpl extends ServiceImpl<BusResourceMemberMapper, BusResourceMemberEntity>
    implements BusResourceMemberRepo {
    private final BusResourceMemberMapping memberMapping;

    @Override
    public boolean add(BusResourceMemberDTO dto) {
        BusResourceMemberEntity entity = memberMapping.dto2Entity(dto);
        entity.setId(null);
        return save(entity);
    }
    @Override
    public void add(List<BusResourceMemberDTO> list, Long folderId) {
        for (BusResourceMemberDTO dto: list){
            BusResourceMemberEntity entity = memberMapping.dto2Entity(dto);
            entity.setId(null);
            entity.setFolderId(Math.toIntExact(folderId));
            this.save(entity);
        }
    }

    @Override
    public boolean delete(Long folderId) {
        return lambdaUpdate()
                .set(BusResourceMemberEntity::getDeleted, true)
                .eq(BusResourceMemberEntity::getFolderId, folderId)
                .eq(BusResourceMemberEntity::getDeleted, false)
                .update(new BusResourceMemberEntity());
    }

    @Override
    public boolean update(BusResourceMemberDTO dto) {
        BusResourceMemberEntity entity = memberMapping.dto2Entity(dto);

        return lambdaUpdate()
                .eq(BusResourceMemberEntity::getId, dto.getId())
                .eq(BusResourceMemberEntity::getDeleted, false)
                .update(entity);
    }

    @Override
    public List<BusResourceMemberDTO> list(Integer folderId) {
        List<BusResourceMemberEntity> list = lambdaQuery()
                .eq(BusResourceMemberEntity::getFolderId, folderId)
                .eq(BusResourceMemberEntity::getDeleted, false)
                .list();
        return Linq.select(list, memberMapping::entity2Dto);
    }

    @Override
    public List<BusResourceMemberDTO> listByUser(String userId) {
        List<BusResourceMemberEntity> list = lambdaQuery()
                .eq(BusResourceMemberEntity::getMemberId, userId)
                .eq(BusResourceMemberEntity::getDeleted, false)
                .list();
        return Linq.select(list, memberMapping::entity2Dto);
    }

    @Override
    public List<BusResourceMemberDTO> listAdminByUser(String userId) {
        List<BusResourceMemberEntity> list = lambdaQuery()
                .eq(BusResourceMemberEntity::getMemberId, userId)
                .eq(BusResourceMemberEntity::getIsAdmin, true)
                .eq(BusResourceMemberEntity::getDeleted, false)
                .list();
        return Linq.select(list, memberMapping::entity2Dto);
    }

    @Override
    public List<BusResourceMemberDTO> listMemberByUser(String userId) {
        List<BusResourceMemberEntity> list = lambdaQuery()
                .eq(BusResourceMemberEntity::getMemberId, userId)
                .eq(BusResourceMemberEntity::getIsAdmin, false)
                // 只要是成员就可以看到该文件夹
//                .eq(BusResourceMemberEntity::getViewAuth, true)
                .eq(BusResourceMemberEntity::getDeleted, false)
                .list();
        return Linq.select(list, memberMapping::entity2Dto);
    }

    @Override
    public List<BusResourceMemberDTO> listMemberAndViewAuthByUser(String userId) {
        List<BusResourceMemberEntity> list = lambdaQuery()
                .eq(BusResourceMemberEntity::getMemberId, userId)
                .eq(BusResourceMemberEntity::getIsAdmin, false)
                .eq(BusResourceMemberEntity::getViewAuth, true)
                .eq(BusResourceMemberEntity::getDeleted, false)
                .list();
        return Linq.select(list, memberMapping::entity2Dto);
    }

    @Override
    public BusResourceMemberDTO getByUser(Integer folderId, String userId) {
        List<BusResourceMemberEntity> list = lambdaQuery()
                .eq(BusResourceMemberEntity::getFolderId, folderId)
                .eq(BusResourceMemberEntity::getMemberId, userId)
                .eq(BusResourceMemberEntity::getDeleted, false)
                .list();
        return memberMapping.entity2Dto(Linq.first(list));
    }

    @Override
    public BusResourceMemberDTO getById(Integer id) {
        List<BusResourceMemberEntity> list = lambdaQuery()
                .eq(BusResourceMemberEntity::getId, id)
                .eq(BusResourceMemberEntity::getDeleted, false)
                .list();
        return memberMapping.entity2Dto(Linq.first(list));
    }
}




