package com.ustack.chat.repo;

import com.ustack.chat.entity.RagflowEntity;
import com.ustack.global.common.rest.RestResponse;

import java.util.Map;

public interface RagflowRepo {
    RestResponse listUser();

    RestResponse registerUser(RagflowEntity ragflowEntity);

    RestResponse inviteUser(RagflowEntity ragflowEntity);

    RestResponse teams();

    RestResponse accept(Map<String, String> params);

    RestResponse delete(String teamId);
}
