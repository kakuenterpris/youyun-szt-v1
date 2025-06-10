package com.ustack.op.runnable;

import cn.hutool.core.util.ObjectUtil;
import com.ustack.emdedding.constants.CommonConstants;
import com.ustack.emdedding.dto.FileUploadRecordDTO;
import com.ustack.feign.client.KbaseApi;
import com.ustack.kbase.entity.CompanyVector;
import com.ustack.kbase.entity.DepartmentVector;
import com.ustack.kbase.entity.PersonalVector;
import com.ustack.op.mapper.FileUploadRecordMapper;
import com.ustack.op.service.EmbeddingService;
import com.ustack.op.util.FileUtils;
import com.ustack.resource.dto.BusResourceManageDTO;
import com.ustack.resource.enums.ResourceCategoryEnum;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

/**
 * @author zhangwei
 * @date 2025年04月09日
 */
@Slf4j
public class SliceAndEmbeddingRunnable implements Runnable {

    private final List<BusResourceManageDTO> resourceList;
    private final KbaseApi kbaseApi;
    private final EmbeddingService embeddingService;

    private final FileUploadRecordMapper fileUploadRecordMapper;

    private final String fileBasePath;


    public SliceAndEmbeddingRunnable(List<BusResourceManageDTO> resourceList, KbaseApi kbaseApi,
                                     EmbeddingService embeddingService, FileUploadRecordMapper fileUploadRecordMapper,
                                     String fileBasePath) {
        this.resourceList = resourceList;
        this.kbaseApi = kbaseApi;
        this.embeddingService = embeddingService;
        this.fileUploadRecordMapper = fileUploadRecordMapper;
        this.fileBasePath = fileBasePath;
    }

    @Override
    public void run() {
        handler();
    }

    private void handler() {
        for (BusResourceManageDTO busResourceManageDTO : resourceList) {
            // 切片并向量化
            this.sliceAndEmbeddingTemp(busResourceManageDTO);
        }
    }

    private void sliceAndEmbeddingTemp(BusResourceManageDTO busResourceManageDTO) {
        // 获取上传的文件路径
        FileUploadRecordDTO fileUploadRecordDTO = fileUploadRecordMapper.getByFileId(busResourceManageDTO.getFileId());
        if (null == fileUploadRecordDTO) {
            log.error("上传文件查询为空，文件id{}", busResourceManageDTO.getFileId());
        }

        String path = fileUploadRecordDTO.getPath();
        String fileName = fileUploadRecordDTO.getFileName();
        // 获取文件流全路径
        path = fileBasePath + File.separator + path + fileName;
        File file = new File(path);
        try {
            MultipartFile multipartFile = FileUtils.convertFileToMultipartFile(file);
            // 调天鸿接口切片
            Map sliceMap = embeddingService.sliceTemp(file, multipartFile.getContentType());
            if (null == sliceMap) {
                log.error("切片为空，文件id{}", busResourceManageDTO.getFileId());
            }
            // 获取文件内容类型
            String fileContentType = (String) sliceMap.get("doc_type");
            // 获取切片内容
            if (null != sliceMap.get("json_success") && !"null".equals(sliceMap.get("json_success"))) {
                List<Map<String, Object>> sliceList = (List<Map<String, Object>>) sliceMap.get("json_success");
                if (null == sliceList) {
                    log.error("切片集合为空，文件id{}", busResourceManageDTO.getFileId());
                }
                // 遍历切片向量化并存储到kbase中
                this.embeddingAndSave(sliceList, busResourceManageDTO, path, fileContentType);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 向量化并保存到kbase中
     *
     * @param sliceList
     * @param busResourceManageDTO
     * @param path
     * @param category
     */
    private void embeddingAndSave(List<Map<String, Object>> sliceList, BusResourceManageDTO busResourceManageDTO, String path, String category) {
        for (Map<String, Object> sliceMap : sliceList) {
            // 向量化处理并存储到kbase
            if (busResourceManageDTO.getCategory().equals(ResourceCategoryEnum.USER.getName())) {
                this.personalStructure(category, sliceMap, busResourceManageDTO, path);
            } else if (busResourceManageDTO.getCategory().equals(ResourceCategoryEnum.DEP.getName())) {
                this.departmentStructure(category, sliceMap, busResourceManageDTO, path);
            } else if (busResourceManageDTO.getCategory().equals(ResourceCategoryEnum.UNIT.getName())) {
                this.institutionStructure(category, sliceMap, busResourceManageDTO, path);
            }
        }
    }

    /**
     * 构造机构向量表并存储
     *
     * @param category
     * @param sliceMap
     * @param busResourceManageDTO
     * @param filePath
     */
    private void institutionStructure(String category, Map<String, Object> sliceMap, BusResourceManageDTO busResourceManageDTO, String filePath) {
        CompanyVector companyVector = new CompanyVector();
        String title = (String) sliceMap.get(CommonConstants.CHUNKS_TITLE_PARAM);
        String section = (String) sliceMap.get(CommonConstants.CHUNKS_SECTION_PARAM);
        String item = (String) sliceMap.get("item");
        String parentContent = (String) sliceMap.get(CommonConstants.CHUNKS_PARENT_PARAM);
        List<String> listChildContent = (List<String>) (sliceMap.get(CommonConstants.CHUNKS_CHILD_PARAM));
        String listChildCotentStr = null;
        if (ObjectUtil.isNotEmpty(listChildContent)) {
            listChildCotentStr = FileUtils.listStringToString(listChildContent);
        }
        companyVector.setTitle(title);
        companyVector.setChapter(section);
        companyVector.setSliceParent(parentContent);
        companyVector.setSliceChild(listChildCotentStr);
        companyVector.setCompanyCode(busResourceManageDTO.getDepNum());
        companyVector.setCompanyName(busResourceManageDTO.getDepName());
        companyVector.setUploadUserId(busResourceManageDTO.getCreateUserId());
        companyVector.setFileId(busResourceManageDTO.getFileId());
        companyVector.setFileName(busResourceManageDTO.getName());
        companyVector.setFileType(busResourceManageDTO.getFileType());
        companyVector.setFilePath(filePath);
        companyVector.setFileContentType(category);
        if (null != busResourceManageDTO.getCreateTime()) {
            companyVector.setUploadTime(CommonConstants.outputFormat.format(busResourceManageDTO.getCreateTime()));
        }

        if (StringUtils.isNotEmpty(title)) {
            String titleEmbedding = embeddingService.embeddingTemp(title);
            companyVector.setTitleVector(titleEmbedding);
        }
        if (StringUtils.isNotEmpty(section)) {
            String sectionEmbedding = embeddingService.embeddingTemp(section);
            companyVector.setChapterVector(sectionEmbedding);
        }
        if (StringUtils.isNotEmpty(parentContent)) {
            String contentEmbedding = embeddingService.embeddingTemp(parentContent);
            companyVector.setSliceParentVector(contentEmbedding);
        }
        if (!ObjectUtil.isEmpty(listChildContent)) {
            String contentEmbedding = embeddingService.embeddingListTemp(listChildContent);
            companyVector.setSliceChildVector(contentEmbedding);
        }
        kbaseApi.insertCompany(companyVector);
    }

    /**
     * 构造部门向量表并存储
     *
     * @param category
     * @param sliceMap
     * @param busResourceManageDTO
     * @param filePath
     */
    private void departmentStructure(String category, Map<String, Object> sliceMap, BusResourceManageDTO busResourceManageDTO, String filePath) {
        DepartmentVector departmentVector = new DepartmentVector();
        String title = (String) sliceMap.get(CommonConstants.CHUNKS_TITLE_PARAM);
        String section = (String) sliceMap.get(CommonConstants.CHUNKS_SECTION_PARAM);
        String item = (String) sliceMap.get("item");
        String edition = (String) sliceMap.get(CommonConstants.CHUNKS_EDITION_PARAM);
        String parentContent = (String) sliceMap.get(CommonConstants.CHUNKS_PARENT_PARAM);
        List<String> listChildContent = (List<String>) (sliceMap.get(CommonConstants.CHUNKS_CHILD_PARAM));
        String listChildCotentStr = null;
        if (ObjectUtil.isNotEmpty(listChildContent)) {
            listChildCotentStr = FileUtils.listStringToString(listChildContent);
        }
        departmentVector.setTitle(title);
        departmentVector.setChapter(section);
        departmentVector.setSliceParent(parentContent);
        departmentVector.setSliceChild(listChildCotentStr);
        departmentVector.setEdition(edition);
        departmentVector.setDepartmentCode(busResourceManageDTO.getDepNum());
        departmentVector.setDepartmentName(busResourceManageDTO.getDepName());
        departmentVector.setUploadUserId(busResourceManageDTO.getCreateUserId());
        departmentVector.setFileId(busResourceManageDTO.getFileId());
        departmentVector.setFileName(busResourceManageDTO.getName());
        departmentVector.setFileType(busResourceManageDTO.getFileType());
        departmentVector.setFilePath(filePath);
        departmentVector.setFileContentType(category);
        if (null != busResourceManageDTO.getCreateTime()) {
            departmentVector.setUploadTime(CommonConstants.outputFormat.format(busResourceManageDTO.getCreateTime()));
        }
        if (StringUtils.isNotEmpty(title)) {
            String titleEmbedding = embeddingService.embeddingTemp(title);
            departmentVector.setTitleVector(titleEmbedding);
        }
        if (StringUtils.isNotEmpty(section)) {
            String sectionEmbedding = embeddingService.embeddingTemp(section);
            departmentVector.setChapterVector(sectionEmbedding);
        }
        if (StringUtils.isNotEmpty(parentContent)) {
            String contentEmbedding = embeddingService.embeddingTemp(parentContent);
            departmentVector.setSliceParentVector(contentEmbedding);
        }
        if (!ObjectUtil.isEmpty(listChildContent)) {
            String contentEmbedding = embeddingService.embeddingListTemp(listChildContent);
            departmentVector.setSliceChildVector(contentEmbedding);
        }
        kbaseApi.insertDepartment(departmentVector);
    }

    /**
     * 构造个人向量表并存储
     *
     * @param category
     * @param sliceMap
     * @param busResourceManageDTO
     * @param filePath
     */
    private void personalStructure(String category, Map<String, Object> sliceMap, BusResourceManageDTO busResourceManageDTO, String filePath) {
        PersonalVector personalVector = new PersonalVector();
        String title = (String) sliceMap.get(CommonConstants.CHUNKS_TITLE_PARAM);
        String section = (String) sliceMap.get(CommonConstants.CHUNKS_SECTION_PARAM);
        // String item = (String) sliceMap.get("item");
        String parentContent = (String) sliceMap.get(CommonConstants.CHUNKS_PARENT_PARAM);
        List<String> listChildContent = (List<String>) sliceMap.get(CommonConstants.CHUNKS_CHILD_PARAM);
        String listChildCotentStr = null;
        if (ObjectUtil.isNotEmpty(listChildContent)) {
            listChildCotentStr = FileUtils.listStringToString(listChildContent);
        }
        personalVector.setTitle(title);
        personalVector.setChapter(section);
        personalVector.setSliceParent(parentContent);
        personalVector.setSliceChild(listChildCotentStr);
        personalVector.setUserId(busResourceManageDTO.getCreateUserId());
        personalVector.setUserName(busResourceManageDTO.getCreateUser());
        personalVector.setDepartmentName(busResourceManageDTO.getDepName());
        personalVector.setFileId(busResourceManageDTO.getFileId());
        personalVector.setFileName(busResourceManageDTO.getName());
        personalVector.setFileType(busResourceManageDTO.getFileType());
        personalVector.setFilePath(filePath);
        personalVector.setFileContentType(category);
        if (null != busResourceManageDTO.getCreateTime()) {
            personalVector.setUploadTime(CommonConstants.outputFormat.format(busResourceManageDTO.getCreateTime()));
        }
        if (StringUtils.isNotEmpty(title)) {
            String titleEmbedding = embeddingService.embeddingTemp(title);
            personalVector.setTitleVector(titleEmbedding);
        }
        if (StringUtils.isNotEmpty(section)) {
            String sectionEmbedding = embeddingService.embeddingTemp(section);
            personalVector.setChapterVector(sectionEmbedding);
        }
        if (StringUtils.isNotEmpty(parentContent)) {
            String contentEmbedding = embeddingService.embeddingTemp(parentContent);
            personalVector.setSliceParentVector(contentEmbedding);
        }
        if (!ObjectUtil.isEmpty(listChildContent)) {
            String contentEmbedding = embeddingService.embeddingListTemp(listChildContent);
            personalVector.setSliceChildVector(contentEmbedding);
        }
        kbaseApi.insertPersonal(personalVector);
    }
}
