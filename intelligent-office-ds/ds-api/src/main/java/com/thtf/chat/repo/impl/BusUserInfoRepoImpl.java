package com.thtf.chat.repo.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.thtf.chat.entity.BusUserInfoEntity;
import com.thtf.chat.mappings.BusUserInfoMapping;
import com.thtf.chat.repo.BusUserInfoRepo;
import com.thtf.chat.mapper.BusUserInfoMapper;
import com.thtf.global.common.utils.Linq;
import com.thtf.global.common.dto.BusUserInfoDTO;
import lombok.RequiredArgsConstructor;
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
}




