package com.ustack.file.repo.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ustack.file.mapper.FileUploadRecordMapper;
import com.ustack.file.repo.IFileUploadRecordRepo;
import com.ustack.file.entity.FileUploadRecordEntity;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.rest.ContextUtil;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

/**
 * @author linxin
 * @Description : 文档上传记录操作
 * @ClassName : FileUploadRecordRepoImpl
 * @Date: 2022-06-22 09:56
 */
@Component
public class FileUploadRecordRepoImpl extends ServiceImpl<FileUploadRecordMapper, FileUploadRecordEntity> implements IFileUploadRecordRepo {

    @Override
    public void saveRecord(FileUploadRecordEntity entity) {
        SystemUser currentUser = ContextUtil.currentUser();
        entity.setCreateUserId(Objects.nonNull(currentUser) ? currentUser.getUserNum() : "");
        entity.setCreateUser(Objects.nonNull(currentUser) ? currentUser.getUserName() : "");
        Date now = new Date();
        entity.setUpdateTime(now);
        entity.setCreateTime(now);
        this.baseMapper.insert(entity);
    }

    @Override
    public FileUploadRecordEntity getByGuid(String guid) {
        LambdaQueryWrapper<FileUploadRecordEntity> q = new LambdaQueryWrapper<>();
        q.eq(FileUploadRecordEntity::getGuid, guid);
        q.eq(FileUploadRecordEntity::getDeleted, false);
        return this.getOne(q);
    }

    @Override
    public FileUploadRecordEntity getByFileMd5(String fileMd5) {
        LambdaQueryWrapper<FileUploadRecordEntity> q = new LambdaQueryWrapper<>();
        q.eq(FileUploadRecordEntity::getMd5, fileMd5);
        q.eq(FileUploadRecordEntity::getDeleted, false);
        // fileMD5查询到多个记录，记录指向的文件应该是同一个
        List<FileUploadRecordEntity> list = this.list(q);
        return (Objects.nonNull(list) && list.size() > 0) ? list.stream().sorted(Comparator.comparing(FileUploadRecordEntity::getId).reversed()).findFirst().orElse(null) : null;
    }

    @Override
    public FileUploadRecordEntity getByFileMd5AndSuffix(String fileMd5, String suffix) {
        LambdaQueryWrapper<FileUploadRecordEntity> q = new LambdaQueryWrapper<>();
        q.eq(FileUploadRecordEntity::getMd5, fileMd5);
        q.eq(FileUploadRecordEntity::getSuffix, suffix);
        q.eq(FileUploadRecordEntity::getDeleted, false);
        // fileMD5查询到多个记录
        List<FileUploadRecordEntity> list = this.list(q);
        return (Objects.nonNull(list) && list.size() > 0) ? list.stream().sorted(Comparator.comparing(FileUploadRecordEntity::getId).reversed()).findFirst().orElse(null) : null;
    }

    @Override
    public Boolean deleteByGuids(List<String> fileGuids) {
       // UserInfo currentUser = ContextUtil.currentUser();
        LambdaUpdateWrapper<FileUploadRecordEntity> u = new LambdaUpdateWrapper<>();
        u.set(FileUploadRecordEntity::getDeleted, true);
        u.set(FileUploadRecordEntity::getUpdateTime, new Date());
       // u.set(FileUploadRecordEntity::getUpdateUser, currentUser.getUserName());
       // u.set(FileUploadRecordEntity::getUpdateUserId, currentUser.getUserId());
        u.in(FileUploadRecordEntity::getGuid, fileGuids);
        boolean update = this.update(u);
        return update;
    }

    /**
     * 根据业务类型同步数据
     *
     * @param businessType
     * @return java.lang.Long
     * @author linxin
     * @date 2022/9/16 09:23
     */
    @Override
    public Long syncFileRecordByBusinessType(String businessType) {
        return this.baseMapper.syncFileRecordByBusinessType(businessType);
    }

    /**
     * 批量查询
     *
     * @param guids
     * @author linxin
     * @date 2022/10/14 15:12
     */
    @Override
    public List<FileUploadRecordEntity> selectByGuids(List<String> guids) {
        LambdaQueryWrapper<FileUploadRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FileUploadRecordEntity::getGuid, guids);
        queryWrapper.eq(FileUploadRecordEntity::getDeleted, false);
        return this.list(queryWrapper);
    }

    /**
     * 根据业务ID批量查询
     *
     * @param params
     * @author linxin
     * @date 2022/11/3 09:21
     */
    @Override
    public List<FileUploadRecordEntity> selectByDocumentIds(List<String> params) {
        LambdaQueryWrapper<FileUploadRecordEntity> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(FileUploadRecordEntity::getDocumentId, params);
        queryWrapper.eq(FileUploadRecordEntity::getDeleted, false);
        return this.list(queryWrapper);
    }
}
