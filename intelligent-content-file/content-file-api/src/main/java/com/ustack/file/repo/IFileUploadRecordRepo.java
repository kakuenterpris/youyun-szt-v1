package com.ustack.file.repo;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.file.entity.FileUploadRecordEntity;

import java.util.List;

/**
 * @author LinXin
 * @DESC 文件记录操作相关接口
 * @Version 1.0
 * @Date 2022/6/22 09:52
 */
public interface IFileUploadRecordRepo extends IService<FileUploadRecordEntity> {

    /**
     * 保存文件记录
     * @param entity
     * @author linxin
     * @date 2022/6/22 09:53
     */
    void saveRecord(FileUploadRecordEntity entity);

    /**
     * 通过文件guid查询
     * @param guid
     * @author linxin
     * @date 2022/6/28 13:51
     */
    FileUploadRecordEntity getByGuid(String guid);


    /**
     * 根据文件md5 查询是否已经有上传成功的记录
     * @param fileMd5
     * @author linxin
     * @date 2022/7/8 13:57
     */
    FileUploadRecordEntity getByFileMd5(String fileMd5);

    /**
     * 增加后缀查询，避免出现上传过无后缀文件，导致重新上传不生成预览图
     * @param fileMd5
     * @author linxin
     * @date 2022/11/7 14:24
     */
    FileUploadRecordEntity getByFileMd5AndSuffix(String fileMd5, String suffix);

    /**
     * 批量删除
     * @param fileGuids
     * @author linxin
     * @return java.lang.Boolean
     * @date 2022/7/6 17:53
     */
    Boolean deleteByGuids(List<String> fileGuids);

    /**
     * 根据业务类型同步数据
     * @param businessType
     * @author linxin
     * @return java.lang.Long
     * @date 2022/9/16 09:23
     */
    Long syncFileRecordByBusinessType(String businessType);

    /**
     * 批量查询
     * @param guids
     * @author linxin
     * @date 2022/10/14 15:12
     */
    List<FileUploadRecordEntity> selectByGuids(List<String> guids);

    /**
     * 根据业务ID批量查询
     * @param params
     * @author linxin
     * @date 2022/11/3 09:21
     */
    List<FileUploadRecordEntity> selectByDocumentIds(List<String> params);

}
