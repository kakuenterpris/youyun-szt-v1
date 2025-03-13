package com.thtf.chat.controller;

import cn.hutool.core.io.file.FileNameUtil;
import com.thtf.chat.enums.ChatApiKeyEnum;
import com.thtf.chat.properties.AiConfigProperties;
import com.thtf.chat.service.ChatService;
import com.thtf.dto.*;
import com.thtf.file.enums.ErrorCodeEnum;
import com.thtf.global.common.rest.RestResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import okhttp3.Response;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 对话接口
 *
 * @author zhangwei
 * @date 2025年02月18日
 */
@RestController
@RequestMapping("/api/v1/chat")
@Slf4j
@RequiredArgsConstructor
@Validated
public class ChatController {

    private final ChatService questionAnswerService;

    @Autowired
    private AiConfigProperties aiConfigProperties;
    private static final String[] whiteFileList = {"TXT", "MARKDOWN", "MDX", "PDF", "HTML", "XLSX", "XLS", "DOCX", "CSV", "MD", "HTM",
            "txt", "markdown", "mdx", "pdf", "html", "xlsx", "xls", "docx", "csv", "md", "htm"};


    /**
     * 创建会话
     * @return
     */
//    @GetMapping("/createConversation")
//    public RestResponse createConversation(@RequestParam("type") String type) {
//        return questionAnswerService.createConversation(type);
//    }

    /**
     * 智能问答接口
     *
     * @param chatRequestDto
     * @return
     */
    @PostMapping(value = "/common", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter common(@RequestBody ChatRequestDto chatRequestDto) {
        if (null != chatRequestDto.getFiles() && chatRequestDto.getFiles().size() > 5) {
            throw new RuntimeException("可上传文件个数不能超过5个");
        }
        return questionAnswerService.common(chatRequestDto);
    }

    /**
     * 推荐问答列表
     *
     * @param recommendChatDto
     * @return
     */
    @PostMapping("/recommendList")
    public RestResponse recommendList(@RequestBody RecommendChatDto recommendChatDto) {
        return questionAnswerService.recommendList(recommendChatDto);
    }
    @PostMapping("/rename")
    public RestResponse rename(@RequestBody RenameChatDto recommendChatDto) {
        return questionAnswerService.renameConversation(recommendChatDto);
    }

    @PostMapping("/stop")
    public RestResponse stopConversation(@RequestBody StopChatDto stopChatDto) {
        return questionAnswerService.stopConversation(stopChatDto);
    }

    /**
     * 上传智能问答文件
     *
     * @param file
     * @param sceneType
     * @return
     */
    @PostMapping(value = "/upload")
    public RestResponse upload(@RequestParam(value = "file") MultipartFile file,
                               @RequestParam(value = "sceneType") String sceneType) {
        String suffix = FileNameUtil.getSuffix(file.getOriginalFilename());
        if (Arrays.stream(whiteFileList).noneMatch(s -> suffix.equals(s))) {
            return RestResponse.error(String.format("%s 文件不支持上传，如有特殊上传需求请联系管理员！", suffix));
        }
        if (file.getSize() > 1024 * 15 * 1024) {
            return RestResponse.error("文件上传最大不能超过15M，如有特殊上传需求请联系管理员！");
        }
        return questionAnswerService.uploadFileToAi(file, sceneType);
    }

//    /**
//     * @return
//     */
//    @GetMapping("/getCnki")
//    public RestResponse getCnki(@RequestParam("query") String query) {
//        String url = aiConfigProperties.getCnkiVectorQueryApi();
//        ApiRequest apiRequest = new ApiRequest(url);
//        apiRequest.setCredential("bc3e6f71963f430bab868284062ab9bd", "cd182fb37ff34effaec1424e6c65482b");
//        apiRequest.addHeaders("Content-Type", "application/json;charset=utf-8");
//        ApiClient apiClient = new ApiClient();
//        CnkiVectorRequestDto cnkiRequestDto = new CnkiVectorRequestDto();
//        cnkiRequestDto.setQuery(query);
//        Gson gson = new Gson();
//        String json = gson.toJson(cnkiRequestDto);
//        apiRequest.setJsonBody(json);
//        apiRequest.setJsonBody(json);
//        try {
//            // String apiResponse = String.valueOf(apiClient.sendRequest(apiRequest));
//            Response response = apiClient.sendRequest(apiRequest);
//            byte[] responseBytes = response.body().bytes();
//            String jsonString = new String(responseBytes);
//            if (StringUtils.isEmpty(jsonString)) {
//                return null;
//            }
//            JSONArray jsonArray = JSONArray.parseArray(jsonString);
//            StringBuffer textBuffer = new StringBuffer();
//            for (int i = 0; i < jsonArray.size(); i++) {
//                if (textBuffer.length() > 2000) {
//                    return RestResponse.success(textBuffer.toString());
//                }
//                JSONObject jsonObject = jsonArray.getJSONObject(i);
//                String text = jsonObject.getString("text");
//                if (textBuffer.length() > 0) {
//                    textBuffer.append("\\r\\n");
//                }
//                textBuffer.append(text);
//                if (textBuffer.length() > 2000) {
//                    String textBufferSubString = textBuffer.substring(0, textBuffer.lastIndexOf("\\r\\n"));
//                    return RestResponse.success(textBufferSubString.toString());
//                }
//            }
//            return RestResponse.success(textBuffer.toString());
//        } catch (Exception e) {
//            log.error("请求知网向量库失败，失败原因：" + e.getMessage());
//            e.getMessage();
//            return null;
//        }
//    }
}
