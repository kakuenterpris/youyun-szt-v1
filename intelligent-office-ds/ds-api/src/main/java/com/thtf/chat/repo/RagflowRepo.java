package com.thtf.chat.repo;

import com.thtf.chat.entity.RagflowEntity;
import com.thtf.global.common.rest.RestResponse;

import java.util.Map;

public interface RagflowRepo {
    RestResponse listUser();

    RestResponse registerUser(RagflowEntity ragflowEntity);

    RestResponse inviteUser(RagflowEntity ragflowEntity);

    RestResponse teams();

    RestResponse accept(Map<String, String> params);

    RestResponse delete(String teamId);
}
