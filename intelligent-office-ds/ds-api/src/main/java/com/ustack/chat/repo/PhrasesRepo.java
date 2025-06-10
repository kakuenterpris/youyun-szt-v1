package com.ustack.chat.repo;

import com.ustack.chat.entity.PhrasesEntity;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ustack.phrases.dto.PhrasesDTO;

import java.util.List;

/**
* @author allm
* @description 针对表【bus_common_phrases(常用语表)】的数据库操作Service
* @createDate 2025-02-18 17:52:56
*/
public interface PhrasesRepo extends IService<PhrasesEntity> {
    /**
     * 添加常用语
     */
    boolean add(PhrasesDTO dto);

    /**
     * 逻辑删除常用语
     */
    boolean delete(Integer id);

    /**
     * 更新常用语
     */
    boolean update(PhrasesDTO dto);

    /**
     * 根据创建人查询常用语列表并根据权重排序
     */
    List<PhrasesDTO> list(String userId);

    /**
     * 根据 ID 查询常用语
     */
    PhrasesDTO getByUserId(String userId);
}
