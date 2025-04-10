package com.thtf.chat.controller;

import com.thtf.chat.service.TestService;
import com.thtf.feign.client.FileApi;
import com.thtf.file.dto.SyncFileDTO;
import com.thtf.global.common.rest.RestResponse;
import com.thtf.global.common.validation.ValidGroup;
import com.thtf.test.TableTestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: DemoController
 * @Date: 2025-02-18 12:28
 */
@RestController
@RequestMapping("/api/v1/demo")
@Slf4j
@RequiredArgsConstructor
@Validated
public class DemoController {

    private final TestService testService;
    private final FileApi fileApi;


    @PostMapping("/insert")
    public RestResponse testInsert(@RequestBody @Validated({ValidGroup.Insert.class}) TableTestDTO param){
        RestResponse restResponse = fileApi.deleteDocument(new SyncFileDTO());
        log.info(restResponse.toString());
        return testService.testInsert(param);
    }

    @PostMapping("/update")
    public RestResponse testUpdate(@RequestBody @Validated({ValidGroup.Update.class}) TableTestDTO param){

        return testService.testUpdate(param);
    }

    @PostMapping("/query")
    public RestResponse testQuery(@RequestBody TableTestDTO param){

        return testService.testQuery(param);
    }

}
