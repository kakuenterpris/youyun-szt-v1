package com.ustack.op.service;

import com.ustack.resource.dto.BusResourceManageDTO;

import java.io.File;
import java.util.List;
import java.util.Map;

public interface EmbeddingService {

    String embedding(String text);

    Map slice(String fileId, String fileType);

    Map sliceTemp(File file, String fileType);

    String embeddingTemp(String content);

    String embeddingListTemp(List<String> listChildContent);
}
