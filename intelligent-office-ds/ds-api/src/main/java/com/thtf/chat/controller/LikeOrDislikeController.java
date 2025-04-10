package com.thtf.chat.controller;


import com.thtf.chat.entity.LikeOrDislikeEntity;
import com.thtf.chat.repo.LikeOrDislikeRepo;
import com.thtf.global.common.rest.RestResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


/**
 * 点赞点踩接口
 * @author zhoufei
 * @date 2025年02月18日
 */

@RestController
@RequestMapping("/api/v1/like/status")
@Slf4j
@RequiredArgsConstructor
@Validated
@Tag(name = "点赞点踩接口", description = "点赞点踩相关操作")
public class LikeOrDislikeController {

    private final LikeOrDislikeRepo likeOrDislikeRepo;


    /**
     * 点赞或点踩
     * @param likeOrDislikeEntity
     * @return
     */
    @PostMapping("/likeOrDislike")
    @Operation(summary = "点赞或点踩")
    public RestResponse likeOrDislike(@RequestBody LikeOrDislikeEntity likeOrDislikeEntity) throws Exception{
        return likeOrDislikeRepo.likeOrDislike(likeOrDislikeEntity);
    }


    /**
     * 查询点赞或点踩状态
     * @param agentAnswerId
     * @return
     */
    @GetMapping("/getLikeOrDislike")
    @Operation(summary = "查询点赞或点踩状态")
    public RestResponse getLikeOrDislike(@RequestParam("agentAnswerId") String agentAnswerId) throws Exception{
        return likeOrDislikeRepo.getLikeOrDislike(agentAnswerId);
    }
}
