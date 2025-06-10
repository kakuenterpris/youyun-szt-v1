package com.ustack.chat.service;

import com.ustack.dto.*;
import com.ustack.global.common.rest.RestResponse;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;

public interface ChatService {
    SseEmitter common(ChatRequestDto questionAnswerDto);

    RestResponse createConversation(String type);

    RestResponse recommendList(RecommendChatDto recommendChatDto);

    RestResponse uploadFileToAi(MultipartFile file, String sceneType);

    RestResponse renameConversation(RenameChatDto recommendChatDto);

    RestResponse stopConversation(StopChatDto stopChatDto);
}
