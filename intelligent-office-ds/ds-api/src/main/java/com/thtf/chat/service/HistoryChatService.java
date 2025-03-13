package com.thtf.chat.service;

import com.thtf.dto.HistoryChatDTO;
import com.thtf.global.common.rest.RestResponse;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @author PingY
 * @Interface HistoryChatService
 * @Description TODO
 * @Date 2025/2/19
 * @Created by PingY
 */
public interface HistoryChatService {

    RestResponse historyChatList(HistoryChatDTO historyChatDTO);

    RestResponse historyChatListDetail(HistoryChatDTO historyChatDTO);

    RestResponse deleteConversations( HistoryChatDTO historyChatDTO);
}
