package com.thtf.chat.service;

import com.thtf.global.common.rest.RestResponse;
import com.thtf.phrases.dto.PhrasesDTO;
import com.thtf.phrases.dto.PhrasesUseLogDTO;

/**
 * @Description: TODO
 * @author：qkh
 * @ClassName: PhrasesService
 * @Date: 2025-02-17 23:57
 */
public interface PhrasesService {

    RestResponse list(String userId);

    RestResponse insert(PhrasesDTO param);


    RestResponse update(PhrasesDTO param);

    RestResponse delete(Integer id);

    RestResponse insertUseLog(PhrasesUseLogDTO param);


}
