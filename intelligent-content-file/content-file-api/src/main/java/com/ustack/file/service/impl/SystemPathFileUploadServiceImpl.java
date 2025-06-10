package com.ustack.file.service.impl;

import cn.hutool.core.codec.Base64;
import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ustack.file.consts.FileProcessRuleConstants;
import com.ustack.file.dto.*;
import com.ustack.file.dto.knowledgeLab.*;
import com.ustack.file.entity.BusResourceFileEntity;
import com.ustack.file.entity.FileUploadRecordEntity;
import com.ustack.file.enums.ErrorCodeEnum;
import com.ustack.file.enums.FileBatchQueryTypeEnum;
import com.ustack.file.mappings.FileMapping;
import com.ustack.file.properties.YouyunProperties;
import com.ustack.file.repo.BusResourceFileRepo;
import com.ustack.file.repo.IFileUploadRecordRepo;
import com.ustack.file.repo.SysOptLogRepo;
import com.ustack.file.service.IFileUploadService;
import com.ustack.file.utils.FileDownloadUrlUtil;
import com.ustack.file.utils.FileNameUtil;
import com.ustack.file.utils.FileUploadRecordGenerator;
import com.ustack.file.utils.FileUtil;
import com.ustack.global.common.dto.SystemUser;
import com.ustack.global.common.exception.CustomException;
import com.ustack.global.common.rest.ContextUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.global.common.utils.JsonUtil;
import com.ustack.global.common.utils.Linq;
import com.ustack.global.common.utils.PDFCheckUtil;
import com.ustack.resource.dto.FileEmbeddingConfigDTO;
import jakarta.annotation.Resource;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.channels.FileChannel;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * @Description : 文件上传 基于服务器物理路径
 * @Author : LinXin
 * @ClassName : SystemPathFileUploadServiceImpl
 * @Date: 2021-03-10 13:11
 */
@Service
@Slf4j
public class SystemPathFileUploadServiceImpl implements IFileUploadService {

    @Value("${file.base.path}")
    private String fileBasePath;

    @Resource
    private YouyunProperties properties;

    IFileUploadRecordRepo iFileUploadRecordRepo;
    FileMapping fileMapping;
    SysOptLogRepo sysOptLogRepo;
    BusResourceFileRepo busResourceFileRepo;


    // 文件操作参数校验
    private static Predicate<FileUploadRecordBaseDTO> fileOperatePredicate = (d) ->
            StringUtils.isBlank(d.getDocumentId()) && StringUtils.isBlank(d.getGuid());


    @Autowired
    public SystemPathFileUploadServiceImpl(IFileUploadRecordRepo iFileUploadRecordRepo, SysOptLogRepo sysOptLogRepo, BusResourceFileRepo busResourceFileRepo) {
        this.iFileUploadRecordRepo = iFileUploadRecordRepo;
        this.sysOptLogRepo = sysOptLogRepo;
        this.busResourceFileRepo = busResourceFileRepo;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse mergeSliceFile(String uuid, String fileMd5, String name, String path) throws Exception {
        // 分片文件保存目录 年/月/日/uuid/
        String sliceDirStr = FileNameUtil.genSliceSaveDir(fileBasePath, uuid);
        // 如果目录不存在 创建分片保存目录
        File sliceSaveDir = new File(sliceDirStr);
        if (!sliceSaveDir.exists()) {
            sliceSaveDir.mkdirs();
        }
        // 文件保存路径
        String fileSaveDir = StringUtils.isBlank(path) ? FileNameUtil.genFileSaveDir(fileBasePath) : fileBasePath + path + File.separator;
        // 文件原始名称
        String originalFilename = name;
        // 合并文件
        final long fileSize = mergeFiles(uuid, sliceSaveDir, fileSaveDir, originalFilename);
        // 保存数据库记录
        FileUploadRecordDTO resultDTO = saveFileRecord(uuid, fileMd5, fileSaveDir, originalFilename, fileSize);
        // 返回文件上传记录信息
        return RestResponse.success(resultDTO);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse uploadFile(MultipartFile file, String fileMd5, String fileId, String path) throws Exception {
        long startTime = System.currentTimeMillis();
        // 生成文件保存路径
        boolean notBlank = StringUtils.isNotBlank(fileId);
        if (notBlank) {
            // 删除旧记录
            iFileUploadRecordRepo.deleteByGuids(Linq.as(fileId));
        }
        //
        String uuid = notBlank ? fileId : IdUtil.simpleUUID();
        // 生成根目录 + 年月日的目录
        String fileSaveDir = StringUtils.isBlank(path) ? FileNameUtil.genFileSaveDir(fileBasePath) : fileBasePath + path + File.separator;
        File dir = new File(fileSaveDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        // 保存到磁盘目标文件的文件路径
        String targetFilePathStr = FileNameUtil.genFileSavePath(fileSaveDir, uuid, FileNameUtil.getSuffix(file.getOriginalFilename()));
        Path targetPathPath = Paths.get(targetFilePathStr);
        long saveFileStartTime = System.currentTimeMillis();
        // 保存文件
        file.transferTo(targetPathPath);
        log.info("文件保存到磁盘耗时：{} ms", System.currentTimeMillis() - saveFileStartTime);
        long getFileMD5StartTime = System.currentTimeMillis();
        if (StringUtils.isBlank(fileMd5)) {
            fileMd5 = FileUtil.getFileMD5(file.getBytes());
        }
        log.info("获取文件MD5耗时：{} ms", System.currentTimeMillis() - getFileMD5StartTime);
        long saveFileRecordStartTime = System.currentTimeMillis();
        // 生成数据库记录
        FileUploadRecordDTO resultDTO = saveFileRecord(uuid, fileMd5, fileSaveDir, file.getOriginalFilename(), file.getSize());
        log.info("保存数据库记录耗时：{} ms", System.currentTimeMillis() - saveFileRecordStartTime);
        log.info("文件上传总耗时：{} ms", System.currentTimeMillis() - startTime);
        // 返回文件信息对象
        return RestResponse.success(resultDTO);
    }

    @Override
    public RestResponse checkSliceFile(String path, String uuid, Integer chunk, String fileMd5, String fileName) throws Exception {
        // 判断是否此md5已经存在
        SliceUploadResponse response = new SliceUploadResponse();
        String suffix = FileNameUtil.getSuffix(fileName);
        FileUploadRecordEntity byFileMd5 = iFileUploadRecordRepo.getByFileMd5AndSuffix(fileMd5, suffix);
        // 可能出现改文件名
        if (Objects.nonNull(byFileMd5)) {
            // 秒传
            byFileMd5.setOriginName(fileName);
            String savePath = StringUtils.isBlank(path) ? FileNameUtil.genFileSaveDir(fileBasePath) : fileBasePath + path + File.separator;
            File dir = new File(savePath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            String recordPath = savePath.replace(fileBasePath, "");
            // 复制文件并生成记录
            FileUploadRecordEntity copiedEntity;
            try {
                copiedEntity = copySingleFile(savePath, recordPath, uuid, byFileMd5);
            } catch (IOException e) {
                return uploadedSlice(uuid, response);
            }
            boolean save = iFileUploadRecordRepo.save(copiedEntity);
            if (save) {
                FileUploadRecordDTO uploadRecordDTO = FileDownloadUrlUtil.getUploadRecordDTO(copiedEntity);
                response.setFile(uploadRecordDTO);
                response.setSkip(true);
                response.setUploaded(Collections.emptyList());
                return RestResponse.success(response);
            }
        }
        return uploadedSlice(uuid, response);
    }

    /**
     * 复制单个文件和记录
     *
     * @param savePath
     * @param recordPath
     * @param entity
     * @author linxin
     * @date 2022/7/8 10:27
     */
    private FileUploadRecordEntity copySingleFile(String savePath, String recordPath, String uuid, FileUploadRecordEntity entity) throws IOException {
        SystemUser currentUser = ContextUtil.currentUser();
        String srcSaveDir = fileBasePath + entity.getPath();
        String srcFilePath = FileNameUtil.genFileSavePath(srcSaveDir, entity.getGuid(), entity.getSuffix());
        String targetFilePath = FileNameUtil.genFileSavePath(savePath, uuid, entity.getSuffix());
        File src = new File(srcFilePath);
        File target = new File(targetFilePath);
        // 拷贝文件
        FileUtil.copyFileNio(src, target);
        // 生成数据库记录
        final FileUploadRecordEntity insert = new FileUploadRecordEntity();
        insert.setSuffix(entity.getSuffix());
        insert.setGuid(uuid);
        insert.setEditKey(IdUtil.simpleUUID());
        insert.setCreateTime(new Date());
        insert.setCreateUser(currentUser.getUserName());
        insert.setCreateUserId(currentUser.getUserNum());
        insert.setUpdateTime(new Date());
        insert.setUpdateUser(currentUser.getUserName());
        insert.setUpdateUserId(currentUser.getUserNum());
        insert.setPath(recordPath);
        insert.setOriginName(entity.getOriginName());
        insert.setFileName(String.format("%s.%s", uuid, entity.getSuffix()));
        insert.setMd5(entity.getMd5());
        insert.setEnableEdit(entity.getEnableEdit());
        insert.setEnablePreview(entity.getEnablePreview());
        insert.setPreviewGenerated(entity.getPreviewGenerated());
        insert.setSize(entity.getSize());
        insert.setStatus(entity.getStatus());
        insert.setEnableDownload(entity.getEnableDownload());
        // 知识库
        insert.setPreviewGenerated(false);
        insert.setDeleted(false);
        return insert;
    }


    private RestResponse uploadedSlice(String uuid, SliceUploadResponse response) {
        // 返回已经上传的分片
        String sliceDirStr = FileNameUtil.genSliceSaveDir(fileBasePath, uuid);
        // 如果目录不存在 创建分片保存目录
        File sliceSaveDir = new File(sliceDirStr);
        if (!sliceSaveDir.exists()) {
            sliceSaveDir.mkdirs();
        }
        List<File> sortedFiles = sortedSubFiles(sliceSaveDir);
        List<String> collect = sortedFiles.stream().map(e -> e.getName()).collect(Collectors.toList());
        response.setFile(new FileUploadRecordDTO());
        response.setSkip(false);
        List<Integer> uploadedChunks = collect.stream().map(e -> Integer.valueOf(e)).collect(Collectors.toList());
        response.setUploaded(uploadedChunks);
        return RestResponse.success(response);
    }

    private List<File> sortedSubFiles(File dir) {
        File[] sliceFileArray = dir.listFiles();
        List<File> sortedFiles = Arrays.stream(sliceFileArray).filter(f -> {
            try {
                Integer.valueOf(f.getName());
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }).sorted((o1, o2) -> Integer.valueOf(o1.getName()).compareTo(Integer.valueOf(o2.getName()))).collect(Collectors.toList());
        return sortedFiles;
    }

    @Override
    public RestResponse uploadSliceFile(MultipartFile file, String uuid, Integer chunk, String fileMd5, String fileName) throws Exception {
        saveSliceFile(file, uuid, chunk);
        return RestResponse.SUCCESS;
    }


    private File saveSliceFile(MultipartFile file, String uuid, Integer chunk) throws IOException {
        if (chunk == null) {
            chunk = 0;
        }
        // 分片文件保存目录 年/月/日/uuid/
        String sliceDirStr = FileNameUtil.genSliceSaveDir(fileBasePath, uuid);
        // 如果目录不存在 创建分片保存目录
        File sliceSaveDir = new File(sliceDirStr);
        if (!sliceSaveDir.exists()) {
            sliceSaveDir.mkdirs();
        }
        // 分片文件保存 名称是分片序号 从 1 开始
        String sliceFileSavePath = FileNameUtil.genSlicePath(sliceDirStr, chunk);
        File sliceFile = new File(sliceFileSavePath);
        // 保存分片文件
        file.transferTo(sliceFile);
        return sliceSaveDir;
    }


    private long mergeFiles(String uuid, File sliceSaveDir, String fileSaveDir, String originalFilename) throws IOException {
        List<File> sortedFiles = sortedSubFiles(sliceSaveDir);
        String suffix = FileNameUtil.getSuffix(originalFilename);
        boolean blank = StringUtils.isBlank(suffix);
        String resolveSuffix = blank ? FileNameUtil.default_format : suffix;
        // 分片全部上传完毕,合并
        final File file = mergeFiles(sortedFiles, fileSaveDir, uuid, resolveSuffix);
        // 删除分片文件
        FileUtil.deleteFile(sliceSaveDir);
        // 如果无文件后缀，删除分片文件之后，把临时后缀去掉重命名为uuid
        if (blank) {
            File rename = cn.hutool.core.io.FileUtil.rename(file, uuid, true);
            return rename.length();
        }
        return file.length();
    }

    /**
     * 文件上传合并成功，保存数据库文件记录
     *
     * @param uuid
     * @param fileMd5
     * @param fileSaveDir
     * @param originalFilename
     * @param fileSize
     * @author linxin
     * @date 2022/6/20 14:32
     */
    private FileUploadRecordDTO saveFileRecord(String uuid, String fileMd5, String fileSaveDir, String originalFilename, long fileSize) throws Exception {
        SystemUser systemUser = ContextUtil.currentUser();
        // 保存没有 basePath 的目录
        FileUploadRecordEntity record = FileUploadRecordGenerator.builder()
                .fileMd5(fileMd5)
                .fileSize(fileSize)
                .fullFileSaveDir(fileSaveDir)
                .fileOriginName(originalFilename)
                .uuid(uuid)
                .fileBasePath(fileBasePath)
                .build().generate();
        record.setCreateUser(Objects.nonNull(systemUser) ? systemUser.getUserName() : "");
        record.setCreateUserId(Objects.nonNull(systemUser) ? systemUser.getUserNum() : "");
        record.setCreateTime(new Date());
        iFileUploadRecordRepo.saveRecord(record);
        return FileDownloadUrlUtil.getUploadRecordDTO(record);
    }

    /**
     * 文件合并 文件名保存为 uuid.suffix
     *
     * @param sortedFiles   根据文件名称排序后的分片文件
     * @param saveDirectory 文件合并后保存的目录
     * @param resolveSuffix uuid
     * @return merged file size
     * @author linxin
     * @date 2021/4/9 10:29
     */
    private File mergeFiles(List<File> sortedFiles, String saveDirectory, String uuid, String resolveSuffix) throws IOException {
        // 创建文件接收合并文件流
        final String filePath = FileNameUtil.genFileSavePath(saveDirectory, uuid, resolveSuffix);
        File file = new File(filePath);
        try (FileOutputStream fos = new FileOutputStream(file);
             FileChannel targetChannel = fos.getChannel()) {
            for (int i = 0; i < sortedFiles.size(); i++) {
                // 读取分片流
                try (FileInputStream fileInputStream = new FileInputStream(sortedFiles.get(i));
                     FileChannel channel = fileInputStream.getChannel()) {
                    // 追加写入
                    channel.transferTo(0, channel.size(), targetChannel);
                }
            }
        }
        return file;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse deleteFile(FileUploadRecordBaseDTO dto) {
        if (fileOperatePredicate.test(dto)) {
            // file guid 业务ID 不能同时为空
            return RestResponse.fail(ErrorCodeEnum.BIZ_ID_FILE_GUID_ISNULL);
        }
        // 构建查询条件
        LambdaQueryWrapper<FileUploadRecordEntity> q = new LambdaQueryWrapper<FileUploadRecordEntity>()
                .eq(StringUtils.isNotBlank(dto.getGuid()), FileUploadRecordEntity::getGuid, dto.getGuid())
                .eq(StringUtils.isNotBlank(dto.getDocumentId()), FileUploadRecordEntity::getDocumentId, dto.getDocumentId())
                .eq(FileUploadRecordEntity::getDeleted, false);
        //
        final List<FileUploadRecordEntity> recordEntities = iFileUploadRecordRepo.list(q);
        if (recordEntities != null && !recordEntities.isEmpty()) {
            List<Long> forUpdate = new ArrayList<>(recordEntities.size() + 1);
            List<Path> delPath = new ArrayList<>(recordEntities.size() + 1);
            for (int i = 0; i < recordEntities.size(); i++) {
                FileUploadRecordEntity entity = recordEntities.get(i);
                Path path = Paths.get(fileBasePath, entity.getPath(), entity.getFileName());
                delPath.add(path);
                // 删除文件
                forUpdate.add(entity.getId());
            }
            // 修改删除状态
            iFileUploadRecordRepo.removeBatchByIds(forUpdate);
            // 删除附件
            FileUtil.deleteFiles(delPath);
            // 返回删除的文件名
            return RestResponse.success(delPath.stream().map(e -> e.getFileName()).collect(Collectors.toList()));
        }
        return RestResponse.success(Collections.emptyList());
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse enableDownload(FileUploadRecordBaseDTO param) {
        LambdaUpdateWrapper<FileUploadRecordEntity> updateWrapper = new LambdaUpdateWrapper<>();
        updateWrapper.set(FileUploadRecordEntity::getEnableDownload, param.getEnableDownload());
        updateWrapper.eq(FileUploadRecordEntity::getEnableDownload, param.getDocumentId());
        updateWrapper.eq(FileUploadRecordEntity::getDeleted, false);
        boolean update = iFileUploadRecordRepo.update(updateWrapper);
        return update ? RestResponse.SUCCESS : RestResponse.fail(ErrorCodeEnum.UPDATE_ENABLE_DOWNLOAD_ERROR);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void getFileStreamByGuid(HttpServletResponse response, String guid) throws UnsupportedEncodingException {
        FileUploadRecordEntity selectOne = iFileUploadRecordRepo.getByGuid(guid);
        if (Objects.nonNull(selectOne) && StringUtils.isNotBlank(selectOne.getPath()) && StringUtils.isNotBlank(selectOne.getSuffix())) {
            String path = fileBasePath + File.separator + selectOne.getPath() + selectOne.getFileName();
            // 获取文件
            response.setContentType("application/x-download");
            response.addHeader("Content-Disposition", "attachment;filename=" + new String(selectOne.getOriginName().getBytes("utf-8"), "ISO8859-1") + "." + selectOne.getSuffix());
            try (InputStream inputStream = java.nio.file.Files.newInputStream(Paths.get(path));
                 ServletOutputStream outputStream = response.getOutputStream()) {
                if (Objects.nonNull(inputStream)) {
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, len);
                    }
                }
            } catch (IOException e) {
                log.error(String.format("获取文件%s流异常:", path), e);
            }

            //记录操作日志
            LambdaQueryWrapper<BusResourceFileEntity> q = new LambdaQueryWrapper<BusResourceFileEntity>()
                    .eq(BusResourceFileEntity::getFileId, guid)
                    .eq(BusResourceFileEntity::getDeleted, false);
            BusResourceFileEntity entity = busResourceFileRepo.getOne(q);
            boolean b = false;
            if (entity != null) {
                b = sysOptLogRepo.saveLog("下载" + entity.getName() + "文件", entity.getId(), Long.valueOf(entity.getFolderId()), "下载", 2);
            }
            log.info("下载文件：{},{}", selectOne.getOriginName(), b ? "成功" : "失败");
        }
    }


    @Override
    public RestResponse uploadBase64(FileUploadBase64DTO dto) throws Exception {
        if (StringUtils.isBlank(dto.getBase64Str()) || StringUtils.isBlank(dto.getFileOriginName())) {
            return RestResponse.fail(ErrorCodeEnum.REQUIRE_PARAMS_ISNULL);
        }
        String base64Str = dto.getBase64Str();
        String suffix = FileNameUtil.getSuffix(dto.getFileOriginName());
        if (StringUtils.isBlank(suffix)) {
            return RestResponse.fail(ErrorCodeEnum.FILE_SUFFIX_REQUIRED);
        }
        // 跟文件夹 fileBasePath + path + 年/月/日
        String newBasePath = fileBasePath + File.separator + Objects.toString(dto.getPath(), File.separator);
        String fileDir = FileNameUtil.genFileSaveDir(newBasePath);
        File dir = new File(fileDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        String uuid = IdUtil.simpleUUID();
        String fileSavePath = FileNameUtil.genFileSavePath(fileDir, uuid, suffix);
        File destFile = new File(fileSavePath);
        Base64.decodeToFile(base64Str, destFile);
        String fileMD5 = FileUtil.getFileMD5(destFile);
        long size = destFile.length();
        FileUploadRecordDTO fileUploadRecordDTO = saveFileRecord(uuid, fileMD5, fileDir, dto.getFileOriginName(), size);
        return RestResponse.success(fileUploadRecordDTO);
    }


    /**
     * 根据类型批量查询
     *
     * @param dto
     * @author linxin
     * @date 2022/11/3 09:11
     */
    @Override
    public RestResponse batchQuery(FileBatchQueryDTO dto) {
        // 判断类型
        if (Objects.isNull(dto.getType())) {
            return RestResponse.fail(ErrorCodeEnum.BATCH_QUERY_TYPE_ISNULL);
        }
        FileBatchQueryTypeEnum typeEnum = FileBatchQueryTypeEnum.getInstance(dto.getType());
        if (Objects.isNull(typeEnum)) {
            return RestResponse.fail(ErrorCodeEnum.BATCH_QUERY_TYPE_INVALID);
        }
        if (Objects.isNull(dto.getParams()) || dto.getParams().isEmpty()) {
            return RestResponse.success(Collections.emptyList());
        }
        switch (typeEnum) {
            case FILE_GUID: {
                List<FileUploadRecordEntity> fileEntities = iFileUploadRecordRepo.selectByGuids(dto.getParams());
                return getBatchQueryResult(fileEntities);
            }
            case FILE_BUSINESS_ID: {
                List<FileUploadRecordEntity> fileEntities = iFileUploadRecordRepo.selectByDocumentIds(dto.getParams());
                return getBatchQueryResult(fileEntities);
            }
            default: {
                return RestResponse.success(Collections.emptyList());
            }
        }
    }

    private RestResponse getBatchQueryResult(List<FileUploadRecordEntity> fileEntities) {
        if (Objects.isNull(fileEntities) || fileEntities.isEmpty()) {
            return RestResponse.success(Collections.emptyList());
        }
        List<FileUploadRecordDTO> recordDTOS = fileEntities.stream().map(e -> {
            try {
                return FileDownloadUrlUtil.getUploadRecordDTO(e);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }).filter(f -> Objects.nonNull(f)).collect(Collectors.toList());
        return RestResponse.success(recordDTOS);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse syncDocument(SyncFileDTO dto) {

        // 根据fileId 查询获取文件位置、documentId等，有documentId表示是修改
        FileUploadRecordEntity entity = iFileUploadRecordRepo.getByGuid(dto.getFileId());
        if (Objects.isNull(entity)) {
            return RestResponse.fail(ErrorCodeEnum.FILE_NOT_EXIST);
        }
        // 获取文件流路劲
        String path = fileBasePath + File.separator + entity.getPath() + entity.getFileName();
        // 组装data json
        SyncFileDataDTO data = defaultConfig(entity.getDocumentId(), dto);

        // 调用dify接口传输json以及文件流
        String json = JsonUtil.toJson(data);

        log.info("json: {}, fileName: {}", json, dto.getFileName());

        // 获取dify返回的文档id，更新到文件记录表
        File file = new File(path);

        String url = String.format(properties.getCreate(), dto.getDatasetId());
        log.info("url:{}", url);
        HttpResponse response = HttpRequest.post(url)
                .header("Authorization", String.format("Bearer %s", properties.getApikey()))
                .form("data", json) // json
                .form("file", file, StringUtils.isNotEmpty(dto.getFileName()) ? dto.getFileName() : entity.getOriginName())
                .execute();
        log.info("status:{}", response.getStatus());
        log.info("body:{}", response.body());
        if (!response.isOk()) {
            log.error("请求接口失败：");
            return RestResponse.fail(ErrorCodeEnum.request_youyun_failed);
        }
        //
        String body = response.body();
        // 解析
        log.info("body: {}", body);
        DocumentWrapper documentWrapper = JsonUtil.fromJson(body, DocumentWrapper.class);
        // 更新documentId
        if (Objects.nonNull(documentWrapper)) {
            Document document = documentWrapper.getDocument();
            if (Objects.nonNull(document)) {
                entity.setDocumentId(document.getId());
            }
            entity.setBatch(documentWrapper.getBatch());
            iFileUploadRecordRepo.updateById(entity);
            try {
                FileUploadRecordDTO uploadRecordDTO = FileDownloadUrlUtil.getUploadRecordDTO(entity);
                if (Objects.nonNull(document)) {
                    uploadRecordDTO.setIndexStatus(document.getIndexing_status());
                }
                return RestResponse.success(uploadRecordDTO);
            } catch (Exception e) {
                throw new CustomException(ErrorCodeEnum.convert_to_record_failed);
            }
        }
        return RestResponse.fail(ErrorCodeEnum.convert_to_record_failed);
    }

    private static SyncFileDataDTO defaultConfig(String documentId, SyncFileDTO dto){
        FileEmbeddingConfigDTO embeddingConfig = dto.getEmbeddingConfig();

        // 处理规则配置
        Segmentation segmentation = Segmentation.builder()
                .separator(StringUtils.isEmpty(embeddingConfig.getSeparatorString()) ? FileProcessRuleConstants.Segmentation.separator : embeddingConfig.getSeparatorString())
                .max_tokens(null == embeddingConfig.getMaxTokens() ? FileProcessRuleConstants.Segmentation.max_tokens : embeddingConfig.getMaxTokens())
                .chunk_overlap(null == embeddingConfig.getChunkOverlap() ? FileProcessRuleConstants.Segmentation.chunk_overlap : embeddingConfig.getChunkOverlap())
                .build();

        List<PreProcessingRule> preProcessingRuleList = new ArrayList<>();
        // 替换连续空格、换行符、制表符
        PreProcessingRule remove_extra_spaces = PreProcessingRule.builder()
                .id(FileProcessRuleConstants.PreProcessingRule.remove_extra_spaces)
                .enabled(null == embeddingConfig.getRemoveExtraSpaces() || 1 == embeddingConfig.getRemoveExtraSpaces())
                .build();
        // 删除 URL、电子邮件地址;
        PreProcessingRule remove_urls_emails = PreProcessingRule.builder()
                .id(FileProcessRuleConstants.PreProcessingRule.remove_urls_emails)
                .enabled(null == embeddingConfig.getRemoveExtraSpaces() || 1 == embeddingConfig.getRemoveUrlsEmails())
                .build();

        preProcessingRuleList.add(remove_extra_spaces);
        preProcessingRuleList.add(remove_urls_emails);

        Rule rules = Rule.builder().segmentation(segmentation)
                .pre_processing_rules(preProcessingRuleList)
                .build();

        ProcessRule processRule = ProcessRule.builder()
                .mode(FileProcessRuleConstants.ProcessMode.custom)
                .rules(rules)
                .build();

        SyncFileDataDTO data = SyncFileDataDTO.builder()
                .original_document_id(documentId)
                .indexing_technique(FileProcessRuleConstants.IndexingTechnique.high)
                .process_rule(processRule)
                .build();

        // 处理规则
        return data;
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public RestResponse checkCanUpload(SyncFileDTO dto) throws IOException {
        // 根据fileId 查询获取文件位置、documentId等，有documentId表示是修改
        FileUploadRecordEntity entity = iFileUploadRecordRepo.getByGuid(dto.getFileId());
        if (Objects.isNull(entity)) {
            return RestResponse.fail(ErrorCodeEnum.FILE_NOT_EXIST);
        }
        // 获取文件流路劲
        String path = fileBasePath + File.separator + entity.getPath() + entity.getFileName();
        // 获取dify返回的文档id，更新到文件记录表
        File file = new File(path);
        boolean doubleLayerPdf = PDFCheckUtil.isDoubleLayerPdf(file);
        return doubleLayerPdf ? RestResponse.success(true) : RestResponse.fail(500, "暂不支持扫描版PDF上传");
    }

    @Override
    public RestResponse deleteDocument(SyncFileDTO dto) {
        // 先调用有云删除接口
        FileUploadRecordEntity entity = iFileUploadRecordRepo.getByGuid(dto.getFileId());
        String url = String.format(properties.getDelete(), dto.getDatasetId(), entity.getDocumentId());
        log.info("url:{}", url);
        HttpResponse response = HttpRequest.post(url)
                .header("Authorization", String.format("Bearer %s", properties.getApikey()))
                .execute();
        if (!response.isOk()){
            return RestResponse.fail(ErrorCodeEnum.request_youyun_failed);
        }
        // 成功后删除文件记录表
        iFileUploadRecordRepo.deleteByGuids(Collections.singletonList(dto.getFileId()));
        // 删除对应物理文件
        String path = fileBasePath + File.separator + entity.getPath() + entity.getFileName();
        File file = new File(path);
        file.delete();

        return RestResponse.okWithMsg("文档删除成功！");
    }

    @Override
    public FileUploadRecordDTO getByFileId(String fileId) {
        return fileMapping.entity2DTO(iFileUploadRecordRepo.getByGuid(fileId));
    }

    public RestResponse getAudioFileByFileId(String fileId) {
        FileUploadRecordEntity selectOne = iFileUploadRecordRepo.getByGuid(fileId);
        if (Objects.nonNull(selectOne) && StringUtils.isNotBlank(selectOne.getPath()) && StringUtils.isNotBlank(selectOne.getSuffix())) {
            String path = fileBasePath + File.separator + selectOne.getPath() + selectOne.getFileName();
            File file = new File(path);
            return RestResponse.success(file);
        }
        return RestResponse.success(null);
    }

    public RestResponse deleteFileCommon(@RequestParam String fileId) {
        FileUploadRecordEntity selectOne = iFileUploadRecordRepo.getByGuid(fileId);
        if (selectOne != null) {
            iFileUploadRecordRepo.deleteByGuids(Collections.singletonList(fileId));
            // 删除服务器文件
            String path = fileBasePath + File.separator + selectOne.getPath() + selectOne.getFileName();
            File file = new File(path);
            file.delete();
        }
        return RestResponse.SUCCESS;
    }
}
