package com.ustack.op.repo;

import com.ustack.op.entity.SysOptLogEntity;
import com.baomidou.mybatisplus.extension.service.IService;

/**
* @author Liyingzheng
* @description 针对表【sys_opt_log(操作日志表)】的数据库操作Service
* @createDate 2025-04-22 16:04:46
*/
public interface SysOptLogRepo extends IService<SysOptLogEntity> {

    boolean saveLog(String operateContent, Long resourceId, Long parentId, String operateType, Integer fileType);

    /**
     * 移动资源时更新
     *
     * @param resourceId        资源id
     * @param originParentId    原父节点id
     * @param newParentId       新父节点id
     */
    boolean updateParentId(Long resourceId, Long originParentId,  Long newParentId);
    boolean saveLogByIdAndParentId(Long resourceId, Long originParentId, Long parentId);
}
