package com.ustack.op.controller;

import com.ustack.global.common.rest.RestResponse;
import com.ustack.op.service.UserTreeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Liyingzheng
 * @data 2025/4/23 11:03
 * @describe
 */
@RestController
@RequestMapping("/api/v1/user/tree")
@RequiredArgsConstructor
public class UserTreeController {

    private final UserTreeService userTreeService;

    @GetMapping("/get")
    public RestResponse get(@RequestParam(value = "searchStr", required = false) String searchStr, @RequestParam(value = "type", required = false) String type, @RequestParam(value = "parentCode", required = false) String parentId) {
        return userTreeService.get(searchStr, type, parentId);
    }
}
