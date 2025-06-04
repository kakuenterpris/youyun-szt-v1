package com.thtf.chat.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.thtf.access.dto.UserInfoDto;
import com.thtf.chat.dto.LogInfoDTO;
import com.thtf.chat.entity.SysOptLogEntity;
import org.apache.ibatis.annotations.Param;

/**
* @author 86187
* @description 针对表【SYS_OPT_LOG(操作日志表)】的数据库操作Mapper
* @createDate 2025-05-28 16:50:16
* @Entity generator.entity.SysOptLogEntity
*/
public interface SysOptLogMapper extends BaseMapper<SysOptLogEntity> {

    Page<LogInfoDTO> getAuditLogs(Page<UserInfoDto> page,@Param("query") String query,@Param("type")String type);
}




