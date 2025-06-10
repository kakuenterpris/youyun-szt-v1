package com.ustack.op.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ustack.op.entity.BusUserInfoEntity;
import com.ustack.op.mapper.BusUserInfoMapper;
import com.ustack.op.mappings.BusUserInfoMapping;
import com.ustack.op.repo.BusUserInfoRepo;
import com.ustack.global.common.dto.BusUserInfoDTO;
import com.ustack.global.common.utils.Linq;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_user_info(人员信息表)】的数据库操作Service实现
* @createDate 2025-02-19 17:45:24
*/
@Service
@RequiredArgsConstructor
public class BusUserInfoRepoImpl extends ServiceImpl<BusUserInfoMapper, BusUserInfoEntity>
    implements BusUserInfoRepo {
    private final BusUserInfoMapping mapping;

    @Override
    public BusUserInfoDTO getByLoginId(String loginId) {
        List<BusUserInfoEntity> list = lambdaQuery()
                .eq(BusUserInfoEntity::getLoginId, loginId)
                .eq(BusUserInfoEntity::getIsDeleted, false)
                .list();
        return mapping.entity2Dto(Linq.first(list));
    }
    @Override
    public BusUserInfoDTO getByEncryptLoginId(String encryptLoginId) {
        List<BusUserInfoEntity> list = lambdaQuery()
                .eq(BusUserInfoEntity::getEncryptLoginId, encryptLoginId)
                .eq(BusUserInfoEntity::getIsDeleted, false)
                .list();
        return mapping.entity2Dto(Linq.first(list));
    }

    @Override
    public boolean deleteAll() {
        return lambdaUpdate()
                .set(BusUserInfoEntity::getIsDeleted, true)
                .eq(BusUserInfoEntity::getIsDeleted, false)
                .update(new BusUserInfoEntity());
    }

    @Override
    public List<BusUserInfoDTO> listAll() {
        List<BusUserInfoEntity> list = lambdaQuery()
                .eq(BusUserInfoEntity::getIsDeleted, false)
                .list();
        return Linq.select(list, mapping::entity2Dto);
    }

    @Override
    public boolean updateEncryptLoginId(String loginId, String encryptLoginId) {
        return lambdaUpdate()
                .set(BusUserInfoEntity::getEncryptLoginId, encryptLoginId)
                .eq(BusUserInfoEntity::getLoginId, loginId)
                .eq(BusUserInfoEntity::getIsDeleted, false)
                .update(new BusUserInfoEntity());
    }

    @Override
    public BusUserInfoDTO getByUserId(String userId) {
        List<BusUserInfoEntity> list = lambdaQuery()
                .eq(BusUserInfoEntity::getUserId, userId)
                .eq(BusUserInfoEntity::getIsDeleted, false)
                .list();
        return mapping.entity2Dto(Linq.first(list));
    }

    @Override
    public Boolean checkAuth(String userId, String auth) {
        if (StringUtils.isEmpty(auth)){
            return false;
        }
        List<BusUserInfoEntity> list = lambdaQuery()
                .eq(BusUserInfoEntity::getUserId, userId)
                .eq(BusUserInfoEntity::getIsDeleted, false)
                .list();
        BusUserInfoEntity first = Linq.first(list);
        return null != first && first.getSpecialAuth().contains(auth);
    }
}




