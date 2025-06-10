package com.ustack.op.runnable;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.ustack.emdedding.dto.FileUploadRecordDTO;
import com.ustack.feign.client.FileApi;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.utils.JsonUtil;
import com.ustack.op.mapper.FileUploadRecordMapper;
import com.ustack.op.repo.BusResourceFileRepo;
import com.ustack.op.repo.BusResourceManageRepo;
import com.ustack.resource.dto.ConvertMarkdownDTO;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2025年04月09日
 */

@Slf4j
public class ConvertMarkdownRunnable implements Runnable {

    private final List<ConvertMarkdownDTO> convertMarkdownDTOS;
    private final FileUploadRecordMapper fileUploadRecordMapper;
    private final BusResourceFileRepo fileRepo;
    private final String fileBasePath;
    private final FileApi fileApi;


    public ConvertMarkdownRunnable(List<ConvertMarkdownDTO> convertMarkdownDTOS,
                                   FileUploadRecordMapper fileUploadRecordMapper,
                                   BusResourceFileRepo fileRepo,
                                   FileApi fileApi,
                                   String fileBasePath) {
        this.convertMarkdownDTOS = convertMarkdownDTOS;
        this.fileUploadRecordMapper = fileUploadRecordMapper;
        this.fileRepo = fileRepo;
        this.fileApi = fileApi;
        this.fileBasePath = fileBasePath;
    }

    @Override
    public void run() {
        this.handler();
    }

    private void handler() {
        for (ConvertMarkdownDTO dto : convertMarkdownDTOS) {
            // 转换md文件
            this.toMarkdown(dto);
        }
    }

    private void toMarkdown(ConvertMarkdownDTO dto) {
        // 获取上传的文件路径
        FileUploadRecordDTO originFile = fileUploadRecordMapper.getByFileId(dto.getFileId());
        File file = new File(fileBasePath + File.separator + originFile.getPath() + originFile.getFileName());
        String content = this.convertToMarkdown(file);
        if (StringUtils.isEmpty(content)){
            return ;
        }

        String previewFileName = originFile.getOriginName().replace("." + originFile.getSuffix(), "") + ".md";
        String previewFileId = IdUtil.simpleUUID();
        // 将字符串转换为字节数组
        byte[] contentBytes = content.getBytes(StandardCharsets.UTF_8);
        // 创建MockMultipartFile对象
        MockMultipartFile mockMultipartFile = new MockMultipartFile(
                previewFileId + ".md",
                previewFileName,
                "text/markdown",
                contentBytes
        );
        RestResponse restResponse = fileApi.updateFile(mockMultipartFile, "", previewFileId, "");
        log.info("预览文件:{}", restResponse.toString());
        fileRepo.updatePreviewFileId(dto.getResourceId(), previewFileId);
    }

    private String convertToMarkdown(File file){
        HttpResponse res = HttpRequest.post("http://10.10.252.165:5098/file_parse")
                .form("file", file)
                .execute();
        String body = res.body();
        log.info("mineru转换结果:{}", body);
        if (StringUtils.isBlank(body)){
            return null;
        }
        Map<String, String> data = (Map<String, String>) JsonUtil.fromJson(body, Map.class);
        return data.get("md_content");
    }
}
