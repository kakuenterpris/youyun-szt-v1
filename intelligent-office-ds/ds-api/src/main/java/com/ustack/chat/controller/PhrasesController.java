package com.ustack.chat.controller;

import com.ustack.chat.service.PhrasesService;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.validation.ValidGroup;
import com.ustack.phrases.dto.PhrasesDTO;
import com.ustack.phrases.dto.PhrasesUseLogDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * @Description: TODO
 * @author：qkh
 * @ClassName: PhrasesController
 * @Date: 2025-02-18 12:28
 */
@RestController
@RequestMapping("/api/v1/chat/phrases")
@Slf4j
@RequiredArgsConstructor
@Validated
public class PhrasesController {

    private final PhrasesService service;


    @PostMapping("/list")
    public RestResponse list(@RequestParam String userId){
        return service.list(userId);
    }

    @PostMapping("/insert")
    public RestResponse insert(@RequestBody @Validated({ValidGroup.Insert.class}) PhrasesDTO param){
        return service.insert(param);
    }

    @PostMapping("/update")
    public RestResponse update(@RequestBody @Validated({ValidGroup.Update.class}) PhrasesDTO param){
        return service.update(param);
    }

    @PostMapping("/delete")
    public RestResponse delete(@RequestParam Integer id){
        return service.delete(id);
    }

    @PostMapping("/insertUseLog")
    public RestResponse insertUseLog(@RequestBody PhrasesUseLogDTO param){
        return service.insertUseLog(param);
    }

}
