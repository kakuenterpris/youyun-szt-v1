package com.ustack.chat.service.impl;

import com.ustack.chat.entity.PhrasesEntity;
import com.ustack.chat.mappings.PhrasesMapping;
import com.ustack.chat.repo.PhrasesCategoryRepo;
import com.ustack.chat.repo.PhrasesRepo;
import com.ustack.chat.repo.PhrasesUseLogRepo;
import com.ustack.chat.service.PhrasesService;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.phrases.dto.PhrasesDTO;
import com.ustack.phrases.dto.PhrasesUseLogDTO;
import com.ustack.phrases.enums.PhrasesErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @Description: TODO
 * @author：qkh
 * @ClassName: PhrasesService
 * @Date: 2025-02-17 23:57
 */
@Service
@RequiredArgsConstructor
public class PhrasesServiceImpl implements PhrasesService {

    private final PhrasesRepo phrasesRepo;
    private final PhrasesCategoryRepo categoryRepo;
    private final PhrasesUseLogRepo useLogRepo;
    private final PhrasesMapping phrasesMapping;


    @Override
    public RestResponse list(String userId) {
        return RestResponse.success(phrasesRepo.list(userId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse insert(PhrasesDTO param) {
        boolean success = phrasesRepo.add(param);

        return success ? RestResponse.success(phrasesRepo.list(ContextUtil.getUserId())) : RestResponse.fail(PhrasesErrorCode.ADD_FAIL);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse update(PhrasesDTO param) {
        PhrasesEntity entity = phrasesRepo.getById(param.getId());
        if (null == entity){
            return RestResponse.fail(PhrasesErrorCode.EDIT_GUID_NOT_EXISTS);
        }
        if (!ContextUtil.getUserId().equals(entity.getCreateUserId())){
            return RestResponse.fail(PhrasesErrorCode.NO_AUTH);
        }

        boolean success = phrasesRepo.update(param);

        return success ? RestResponse.success(phrasesRepo.list(ContextUtil.getUserId())) : RestResponse.fail(PhrasesErrorCode.EDIT_FAIL);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse delete(Integer id) {
        PhrasesEntity entity = phrasesRepo.getById(id);
        if (null == entity){
            return RestResponse.fail(PhrasesErrorCode.DELETE_GUID_NOT_EXISTS);
        }
        if (!ContextUtil.getUserId().equals(entity.getCreateUserId())){
            return RestResponse.fail(PhrasesErrorCode.NO_AUTH);
        }

        boolean success = phrasesRepo.delete(id);

        return success ? RestResponse.success(phrasesRepo.list(ContextUtil.getUserId())) : RestResponse.fail(PhrasesErrorCode.DELETE_FAIL);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse insertUseLog(PhrasesUseLogDTO param) {
        PhrasesEntity entity = phrasesRepo.getById(param.getPhraseId());
        if (null != entity){
            useLogRepo.add(param);
            entity.setWeight(useLogRepo.count(param.getPhraseId()));
            phrasesRepo.update(phrasesMapping.entity2Dto(entity));
        }
        return RestResponse.SUCCESS;
    }
}
