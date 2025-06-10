package com.ustack.kbase.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.github.pagehelper.util.StringUtil;
import com.ustack.global.common.rest.RestResponse;
import com.ustack.kbase.entity.CompanyVector;
import com.ustack.kbase.entity.DepartmentVector;
import com.ustack.kbase.entity.KmVector;
import com.ustack.kbase.entity.PersonalVector;
import com.ustack.kbase.enums.KbaseTableEnum;
import com.ustack.kbase.service.KbaseService;
import com.ustack.kbase.util.KbaseStatementUtil;
import com.ustack.kbase.util.ProcessUtil;
import lombok.extern.slf4j.Slf4j;
import net.cnki.kbase.jdbc.KbaseDataSource;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.*;

/**
 * @author zhangwei
 * @date 2025年03月25日
 */
@Slf4j
@Service
public class KbaseServiceImpl implements KbaseService {

    @Resource
    private KbaseDataSource kbaseDataSource;

    /**
     * 构造插入数据SQL
     *
     * @param kmVector
     * @return
     */
    private String constructInsertSQL(KmVector kmVector) {
        // 对数据双引号进行转义处理
        kmVector.setFileName(ProcessUtil.escapeHtml2(kmVector.getFileName()));
        kmVector.setTitle(ProcessUtil.escapeHtml2(kmVector.getTitle()));
        kmVector.setChapter(ProcessUtil.escapeHtml2(kmVector.getChapter()));
        kmVector.setSliceParent(ProcessUtil.escapeHtml2(kmVector.getSliceParent()));
        kmVector.setSliceChild(ProcessUtil.escapeHtml2(kmVector.getSliceChild()));
        kmVector.setKeywords(ProcessUtil.escapeHtml2(kmVector.getKeywords()));
        StringBuffer insertSql = new StringBuffer();
        insertSql.append("INSERT INTO ")
                .append(KbaseTableEnum.TFGF_KM202504.getName())
                .append("(")
                .append("文件ID,文件名称,文件格式类型,文件内容类型,文件上传时间,上传用户ID,上传用户,上传用户部门,上传用户部门编码,标题文本,章节文本,父段文本,子段文本,标题向量,章节向量,父段向量,子段向量,版次,关键词,字符数,文件年份,文件月份,现行有效,CHUNK_ID,文件夹ID")
                .append(")")
                .append("values")
                .append("(")
                .append("\"").append(kmVector.getFileId()).append("\"").append(",")
                .append("\"").append(kmVector.getFileName()).append("\"").append(",")
                .append("\"").append(kmVector.getFileType()).append("\"").append(",")
                .append("\"").append(kmVector.getFileContentType()).append("\"").append(",")
                .append("\"").append(kmVector.getUploadTime()).append("\"").append(",")
                .append("\"").append(kmVector.getUserId()).append("\"").append(",")
                .append("\"").append(kmVector.getUserName()).append("\"").append(",")
                .append("\"").append(kmVector.getDepartmentName()).append("\"").append(",")
                .append("\"").append(kmVector.getDepartmentNum()).append("\"").append(",")
                .append("\"").append(kmVector.getTitle()).append("\"").append(",")
                .append("\"").append(kmVector.getChapter()).append("\"").append(",")
                .append("\"").append(kmVector.getSliceParent()).append("\"").append(",")
                .append("\"").append(kmVector.getSliceChild()).append("\"").append(",")
                .append("\"").append(kmVector.getTitleVector()).append("\"").append(",")
                .append("\"").append(kmVector.getChapterVector()).append("\"").append(",")
                .append("\"").append(kmVector.getSliceParentVector()).append("\"").append(",")
                .append("\"").append(kmVector.getSliceChildVector()).append("\"").append(",")
                .append("\"").append(kmVector.getEdition()).append("\"").append(",")
                .append("\"").append(kmVector.getKeywords()).append("\"").append(",")
                .append(kmVector.getWordCount()).append(",")
                .append(kmVector.getYear()).append(",")
                .append("\"").append(kmVector.getMonth()).append("\"").append(",")
                .append("\"").append(kmVector.getValid()).append("\"").append(",")
                .append("\"").append(kmVector.getChunkId()).append("\"").append(",")
                .append("\"").append(kmVector.getFolderId()).append("\"")
                .append(")");
        return insertSql.toString();
    }

    /**
     * 企业向量表插入数据
     *
     * @param companyVector
     * @return
     */
    @Override
    public boolean insertCompany(CompanyVector companyVector) {
        if (null == companyVector || StringUtil.isEmpty(companyVector.getFileId())) {
            return false;
        }
        String sql = constructInsertCompanySQL(companyVector);
        KbaseStatementUtil kbaseStatementUtil = new KbaseStatementUtil();
        if (kbaseStatementUtil.execute(sql, kbaseDataSource)) {
            return true;
        }
        return false;
    }

    /**
     * 根据文件id删除数据
     *
     * @param fileId
     * @return
     */
    @Override
    public boolean deleteCompany(String fileId) {
        if (StringUtil.isEmpty(fileId)) {
            return false;
        }
        String sql = constructDeleteSQL(fileId, KbaseTableEnum.COMPANY.getName());
        KbaseStatementUtil kbaseStatementUtil = new KbaseStatementUtil();
        if (kbaseStatementUtil.execute(sql, kbaseDataSource)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteDepartment(String fileId) {

        String sql = constructDeleteSQL(fileId, KbaseTableEnum.DEPARTMENT.getName());
        KbaseStatementUtil kbaseStatementUtil = new KbaseStatementUtil();
        if (kbaseStatementUtil.execute(sql, kbaseDataSource)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deletePersonal(String fileId) {
        String sql = constructDeleteSQL(fileId, KbaseTableEnum.PERSONAL.getName());
        KbaseStatementUtil kbaseStatementUtil = new KbaseStatementUtil();
        if (kbaseStatementUtil.execute(sql, kbaseDataSource)) {
            return true;
        }
        return false;
    }

    /**
     * 企业表查询
     *
     * @param tableName
     * @param condition
     * @param companyId
     * @return
     */
    @Override
    public Map selectCompanyExecute(String tableName, String condition, String companyId) {
        StringBuffer selectSql = new StringBuffer();
        selectSql.append("select GETSYSFIELD ('__RELEVANT') as relevance,GETVECDIST ('L2')as distRelevance," +
                        " 文件ID as fileId,文件名称 as fileName,文件格式类型 as suffix,父段文本 as context,关键词 as keywords ")
                .append(" from ")
                .append(tableName)
                .append(" where 父段向量 = ").append("\"").append(condition).append("\"")
                .append(" and ")
                .append(" 企业代码 = ").append("\"").append(companyId).append("\"")
                .append(" order by ").append(" distRelevance asc ");
        return this.selectDataHandler(selectSql.toString());
    }

    /**
     * 个人表查询
     *
     * @param tableName
     * @param condition
     * @param useId
     * @return
     */
    @Override
    public Map selectPersonalExecute(String tableName, String condition, String useId) {
        StringBuffer selectSql = new StringBuffer();
        selectSql.append("select  GETSYSFIELD ('__RELEVANT') as relevance,GETVECDIST ('L2')as distRelevance," +
                        " 文件ID as fileId,文件名称 as fileName,文件格式类型 as suffix,父段文本 as context,关键词 as keywords ")
                .append(" from ")
                .append(tableName)
                .append(" where 父段向量 = ").append("\"").append(condition).append("\"")
                .append(" and ")
                .append(" 用户ID = ").append("\"").append(useId).append("\"")
                .append(" order by ").append(" distRelevance asc ");
        return this.selectDataHandler(selectSql.toString());
    }

    /**
     * 部门表查询
     *
     * @param tableName
     * @param condition
     * @param depNum
     * @return
     */
    @Override
    public Map selectDepartmentExecute(String tableName, String condition, String depNum) {
        StringBuffer selectSql = new StringBuffer();
        selectSql.append("select GETSYSFIELD ('__RELEVANT') as relevance,GETVECDIST ('L2')as distRelevance," +
                        " 文件ID as fileId,文件名称 as fileName,文件格式类型 as suffix,父段文本 as context,关键词 as keywords ")
                .append(" from ")
                .append(tableName)
                .append(" where 父段向量 = ").append("\"").append(condition).append("\"")
                .append(" and ")
                .append("部门代码 = ").append("\"").append(depNum).append("\"")
                .append(" order by ").append(" distRelevance asc ");
        return this.selectDataHandler(selectSql.toString());
    }

    private Map selectDataHandler(String sql) {
        try {
            KbaseStatementUtil kbaseStatementUtil = new KbaseStatementUtil();
            Map resultMap = kbaseStatementUtil.executeQuery(sql, kbaseDataSource);
            ;
            return resultMap;
        } catch (Exception e) {
            e.printStackTrace();
            log.error("个人向量表查询失败，失败原因{}", e);
            throw new RuntimeException(e);
        }
    }


    private String constructDeleteSQL(String fileId, String tableName) {
        StringBuffer deleteSql = new StringBuffer();
        deleteSql.append("delete from ")
                .append(tableName)
                .append(" where 文件ID = ").append(fileId);
        return deleteSql.toString();
    }

    /**
     * 部门向量表插入数据
     *
     * @param departmentVector
     * @return
     */
    @Override
    public boolean insertDepartment(DepartmentVector departmentVector) {
        if (null == departmentVector || StringUtil.isEmpty(departmentVector.getFileId())) {
            return false;
        }
        /**
         * 调用组装SQL方法
         */
        String sql = constructInsertDepartmentSQL(departmentVector);
        KbaseStatementUtil kbaseStatementUtil = new KbaseStatementUtil();
        if (kbaseStatementUtil.execute(sql, kbaseDataSource)) {
            return true;
        }
        return false;
    }

    /**
     * 个人向量表插入数据
     *
     * @param personalVector
     * @return
     */
    @Override
    public boolean insertPersonal(PersonalVector personalVector) {
        if (null == personalVector || StringUtil.isEmpty(personalVector.getFileId())) {
            return false;
        }
        /**
         * 调用组装SQL方法
         */
        String sql = constructInsertPersonalSQL(personalVector);
        KbaseStatementUtil kbaseStatementUtil = new KbaseStatementUtil();
        if (kbaseStatementUtil.execute(sql, kbaseDataSource)) {
            return true;
        }
        return false;
    }

    /**
     * 知识库查询
     *
     * @param query
     * @return
     */
    @Override
    public RestResponse getKnowledge(String query) {
        List<Map> resultList = new ArrayList<>();
        // 查个人知识库
        Map personalMap = this.selectPersonalExecute(KbaseTableEnum.PERSONAL.getName(), query, null);
        if (null != personalMap) {
            List<Map> list = (List<Map>) personalMap.get("data");
            this.handlerSelectData(list, resultList);

        }
        // 查部门知识库
        Map departmentMap = this.selectDepartmentExecute(KbaseTableEnum.DEPARTMENT.getName(), query, null);
        if (null != departmentMap) {
            List<Map> list = (List<Map>) departmentMap.get("data");
            this.handlerSelectData(list, resultList);

        }
        // 查组织知识库
        Map companyMap = this.selectCompanyExecute(KbaseTableEnum.COMPANY.getName(), query, null);
        if (null != companyMap) {
            List<Map> list = (List<Map>) companyMap.get("data");
            this.handlerSelectData(list, resultList);
        }
        return RestResponse.success(resultList);
    }

    /**
     * 根据向量化内容查询
     *
     * @param embedding
     */
    @Override
    public Map queryPersonalByEmbedding(String userId, String embedding) {
        if (StrUtil.isEmpty(embedding) || StrUtil.isEmpty(userId)) {
            return null;
        }
        // List<Map> resultList = new ArrayList<>();
        Map personalMap = this.selectPersonalExecute(KbaseTableEnum.PERSONAL.getName(), embedding, userId);
//        if (null != personalMap) {
//            List<Map> list = (List<Map>) personalMap.get("data");
//            this.handlerSelectData(list, resultList);
//
//        }
        return personalMap;

    }

    @Override
    public Map queryDepartmentByEmbedding(String deptNum, String embedding) {
        if (StrUtil.isEmpty(embedding) || StrUtil.isEmpty(deptNum)) {
            return null;
        }
        // List<Map> resultList = new ArrayList<>();
        Map departmentMap = this.selectDepartmentExecute(KbaseTableEnum.DEPARTMENT.getName(), embedding, deptNum);
//        if (null != personalMap) {
//            List<Map> list = (List<Map>) personalMap.get("data");
//            this.handlerSelectData(list, resultList);
//
//        }
        return departmentMap;

    }

    @Override
    public Map queryCompanyByEmbedding(String companyNum, String embedding) {
        if (StrUtil.isEmpty(embedding) || StrUtil.isEmpty(companyNum)) {
            return null;
        }
        // List<Map> resultList = new ArrayList<>();
        Map companyMap = this.selectCompanyExecute(KbaseTableEnum.COMPANY.getName(), embedding, companyNum);
//        if (null != personalMap) {
//            List<Map> list = (List<Map>) personalMap.get("data");
//            this.handlerSelectData(list, resultList);
//
//        }
        return companyMap;
    }

    /**
     * 插入数据
     *
     * @param kmVector
     * @return
     */
    @Override
    public boolean insert(KmVector kmVector) {
        if (null == kmVector || StringUtil.isEmpty(kmVector.getFileId())) {
            return false;
        }
        /**
         * 调用组装SQL方法
         */
        String sql = constructInsertSQL(kmVector);
        KbaseStatementUtil kbaseStatementUtil = new KbaseStatementUtil();
        if (kbaseStatementUtil.execute(sql, kbaseDataSource)) {
            return true;
        }
        return false;
    }

    /**
     * 根据文件id删除知识库数据
     *
     * @param fileId
     * @return
     */
    @Override
    public boolean deleteByFileId(String fileId) {
        if (StrUtil.isEmpty(fileId)) {
            return false;
        }
        String sql = constructDeleteSQL(fileId, KbaseTableEnum.TFGF_KM202504.getName());
        KbaseStatementUtil kbaseStatementUtil = new KbaseStatementUtil();
        if (kbaseStatementUtil.execute(sql, kbaseDataSource)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteByFolderId(String folderId) {
        if (StrUtil.isEmpty(folderId)) {
            return false;
        }
        String sql = constructDeleteByFolderIdSQL(folderId, KbaseTableEnum.TFGF_KM202504.getName());
        KbaseStatementUtil kbaseStatementUtil = new KbaseStatementUtil();
        if (kbaseStatementUtil.execute(sql, kbaseDataSource)) {
            return true;
        }
        return false;
    }

    /**
     * 更新知识库是否加入问答
     *
     * @param fileIdList
     * @param joinValue
     * @return
     */
    @Override
    public boolean updateJoinValid(List<String> fileIdList, Integer joinValue) {
        if (CollUtil.isEmpty(fileIdList) || null == joinValue) {
            log.error("更新知识库是否加入问答失败，fileIdList为空或者joinValue为空");
            return false;
        }
        StringBuffer fileIdBuffer = new StringBuffer();
        for (String  fileId : fileIdList) {
            if (fileIdBuffer.length() > 0) {
                fileIdBuffer.append("+");
            }
            fileIdBuffer.append("\"").append(fileId).append("\"");
        }
        StringBuffer updateSql = new StringBuffer();
        updateSql.append("update ")
                .append(KbaseTableEnum.TFGF_KM202504.getName())
                .append(" set ")
                .append(" 现行有效 = ").append(joinValue)
                .append(" where 文件ID = ").append(fileIdBuffer.toString());
        KbaseStatementUtil kbaseStatementUtil = new KbaseStatementUtil();
        if (kbaseStatementUtil.execute(updateSql.toString(), kbaseDataSource)) {
            return true;
        }
        return false;
    }

    private String constructDeleteByFolderIdSQL(String folderId, String tableName) {
        StringBuffer deleteSql = new StringBuffer();
        deleteSql.append("delete from ")
                .append(tableName)
                .append(" where 文件夹ID = ").append(folderId);
        return deleteSql.toString();
    }

    /**
     * 根据向量化内容查询知识库数据
     *
     * @param fileIdList
     * @param embedding
     * @return
     */
    @Override
    public Map queryByEmbedding(List<String> fileIdList, String embedding) {
        if (StrUtil.isEmpty(embedding) || CollUtil.isEmpty(fileIdList)) {
            return null;
        }
        StringBuffer fileIdBuffer = new StringBuffer();
        for (String fileId : fileIdList) {
            if (fileIdBuffer.length() > 0) {
                fileIdBuffer.append(" + ");
            }
            fileIdBuffer.append("\"").append(fileId).append("\"");
        }
        Map dataMap = this.selectExecute(embedding, fileIdBuffer.toString());
        return dataMap;
    }

    @Override
    public Map queryByEmbeddingAndFolderIds(List<String> folderIdIdList, String embedding) {
        if (StrUtil.isEmpty(embedding) || CollUtil.isEmpty(folderIdIdList)) {
            return null;
        }
        StringBuffer folderIdBuffer = new StringBuffer();
        for (String folderId : folderIdIdList) {
            if (folderIdBuffer.length() > 0) {
                folderIdBuffer.append(" + ");
            }
            folderIdBuffer.append("\"").append(folderId).append("\"");
        }
        Map dataMap = this.selectExecuteByFolderId(embedding, folderIdBuffer.toString());
        return dataMap;
    }

    private Map selectExecuteByFolderId(String embedding, String folderIdStr) {
        StringBuffer selectSql = new StringBuffer();
        selectSql.append("select GETSYSFIELD ('__RELEVANT') as relevance,GETVECDIST ('L2')as distRelevance," +
                        " 文件ID as fileId,文件名称 as fileName,文件格式类型 as suffix,父段文本 as context,关键词 as keywords ")
                .append(" from ")
                .append(KbaseTableEnum.TFGF_KM202504.getName())
                .append(" where 父段向量 = ").append("\"").append(embedding).append("\"")
                .append(" and ")
                .append(" 文件夹ID = ").append(folderIdStr)
                .append(" and ")
                .append(" 现行有效 = 1")
                .append(" order by ").append(" distRelevance asc ");
        return this.selectDataHandler(selectSql.toString());
    }

    private Map selectExecute(String embedding, String fieldIdStr) {
        StringBuffer selectSql = new StringBuffer();
        selectSql.append("select GETSYSFIELD ('__RELEVANT') as relevance,GETVECDIST ('L2')as distRelevance," +
                        " 文件ID as fileId,文件名称 as fileName,文件格式类型 as suffix,父段文本 as context,关键词 as keywords ")
                .append(" from ")
                .append(KbaseTableEnum.TFGF_KM202504.getName())
                .append(" where 父段向量 = ").append("\"").append(embedding).append("\"")
                .append(" and ")
                .append(" 文件ID = ").append(fieldIdStr)
                .append(" and ")
                .append(" 现行有效 = 1")
                .append(" order by ").append(" distRelevance asc ");
        return this.selectDataHandler(selectSql.toString());
    }

    private void handlerSelectData(List<Map> list, List<Map> resultList) {
        if (ObjectUtil.isNotEmpty(list)) {
            // 获取所有fileId
            Set<String> fileIdSet = new HashSet<>();
            for (Map map : list) {
                fileIdSet.add((String) map.get("fileId"));
            }
            for (String fileId : fileIdSet) {
                StringBuffer contentBuffer = new StringBuffer();
                Map dataMap = null;
                for (int i = 0; i < list.size(); i++) {
                    Map map = list.get(i);
                    String currentFileId = (String) map.get("fileId");
                    String fileName = (String) map.get("fileName");
                    String suffix = (String) map.get("suffix");
                    String context = (String) map.get("context");
                    if (currentFileId.equals(fileId)) {
                        if (contentBuffer.length() > 0) {
                            contentBuffer.append("\\r\\n");
                        }
                        contentBuffer.append(context);
                        if (i == list.size() - 1) {
                            dataMap = new HashMap();
                            dataMap.put("title", fileName + "." + suffix);
                            dataMap.put("context", contentBuffer.toString());
                            dataMap.put("documentId", fileId);
                            resultList.add(dataMap);
                        }
                    } else if (i != list.size() - 1) {
                        dataMap = new HashMap();
                        dataMap.put("title", fileName + "." + suffix);
                        dataMap.put("context", contentBuffer.toString());
                        dataMap.put("documentId", fileId);
                        resultList.add(dataMap);
                    } else {
                        dataMap = new HashMap();
                        dataMap.put("title", fileName + "." + suffix);
                        dataMap.put("context", contentBuffer.toString());
                        dataMap.put("documentId", fileId);
                        resultList.add(dataMap);
                    }
                }
            }
        }
    }

    private String constructInsertCompanySQL(CompanyVector companyVector) {
        StringBuffer insertSql = new StringBuffer();
        insertSql.append("INSERT INTO ")
                .append(KbaseTableEnum.COMPANY.getName())
                .append("(")
                .append("企业代码,企业名称,文件名称,文件ID,文件格式类型,文件内容类型,文件上传时间,上传用户ID,上传用户部门,文件存储路径,标题文本,章节文本,父段文本,子段文本,标题向量,章节向量,父段向量,子段向量,版次,关键词,字符数")
                .append(")")
                .append("values")
                .append("(")
                .append("\"").append(companyVector.getCompanyCode()).append("\"").append(",")
                .append("\"").append(companyVector.getCompanyName()).append("\"").append(",")
                .append("\"").append(companyVector.getFileName()).append("\"").append(",")
                .append("\"").append(companyVector.getFileId()).append("\"").append(",")
                .append("\"").append(companyVector.getFileType()).append("\"").append(",")
                .append("\"").append(companyVector.getFileContentType()).append("\"").append(",")
                .append("\"").append(companyVector.getUploadTime()).append("\"").append(",")
                .append("\"").append(companyVector.getUploadUserId()).append("\"").append(",")
                .append("\"").append(companyVector.getUploadUserDepartment()).append("\"").append(",")
                .append("\"").append(companyVector.getFilePath()).append("\"").append(",")
                .append("\"").append(companyVector.getTitle()).append("\"").append(",")
                .append("\"").append(companyVector.getChapter()).append("\"").append(",")
                .append("\"").append(companyVector.getSliceParent()).append("\"").append(",")
                .append("\"").append(companyVector.getSliceChild()).append("\"").append(",")
                .append("\"").append(companyVector.getTitleVector()).append("\"").append(",")
                .append("\"").append(companyVector.getChapterVector()).append("\"").append(",")
                .append("\"").append(companyVector.getSliceParentVector()).append("\"").append(",")
                .append("\"").append(companyVector.getSliceChildVector()).append("\"").append(",")
                .append("\"").append(companyVector.getEdition()).append("\"").append(",")
                .append("\"").append(companyVector.getKeywords()).append("\"").append(",")
                .append("\"").append(companyVector.getWordCount()).append("\"")
                .append(")");
        return insertSql.toString();
    }

    private String constructInsertDepartmentSQL(DepartmentVector departmentVector) {
        StringBuffer insertSql = new StringBuffer();
        insertSql.append("INSERT INTO ")
                .append(KbaseTableEnum.DEPARTMENT.getName())
                .append("(")
                .append("部门代码,部门名称,所在单位代码,所在单位名称,文件名称,文件ID,文件格式类型,文件内容类型,文件上传时间,上传用户ID,上传用户部门,文件存储路径,标题文本,章节文本,父段文本,子段文本,标题向量,章节向量,父段向量,子段向量,版次,关键词,字符数")
                .append(")")
                .append("values")
                .append("(")
                .append("\"").append(departmentVector.getDepartmentCode()).append("\"").append(",")
                .append("\"").append(departmentVector.getDepartmentName()).append("\"").append(",")
                .append("\"").append(departmentVector.getCompanyCode()).append("\"").append(",")
                .append("\"").append(departmentVector.getCompanyName()).append("\"").append(",")
                .append("\"").append(departmentVector.getFileName()).append("\"").append(",")
                .append("\"").append(departmentVector.getFileId()).append("\"").append(",")
                .append("\"").append(departmentVector.getFileType()).append("\"").append(",")
                .append("\"").append(departmentVector.getFileContentType()).append("\"").append(",")
                .append("\"").append(departmentVector.getUploadTime()).append("\"").append(",")
                .append("\"").append(departmentVector.getUploadUserId()).append("\"").append(",")
                .append("\"").append(departmentVector.getUploadUserDepartment()).append("\"").append(",")
                .append("\"").append(departmentVector.getFilePath()).append("\"").append(",")
                .append("\"").append(departmentVector.getTitle()).append("\"").append(",")
                .append("\"").append(departmentVector.getChapter()).append("\"").append(",")
                .append("\"").append(departmentVector.getSliceParent()).append("\"").append(",")
                .append("\"").append(departmentVector.getSliceChild()).append("\"").append(",")
                .append("\"").append(departmentVector.getTitleVector()).append("\"").append(",")
                .append("\"").append(departmentVector.getChapterVector()).append("\"").append(",")
                .append("\"").append(departmentVector.getSliceParentVector()).append("\"").append(",")
                .append("\"").append(departmentVector.getSliceChildVector()).append("\"").append(",")
                .append("\"").append(departmentVector.getEdition()).append("\"").append(",")
                .append("\"").append(departmentVector.getKeywords()).append("\"").append(",")
                .append("\"").append(departmentVector.getWordCount()).append("\"")
                .append(")");
        return insertSql.toString();
    }

    private String constructInsertPersonalSQL(PersonalVector personalVector) {
        StringBuffer insertSql = new StringBuffer();
        insertSql.append("INSERT INTO  ")
                .append(KbaseTableEnum.PERSONAL.getName())
                .append("(")
                .append("用户ID,用户名,所在单位代码,所在单位名称,所在一级部门,文件名称,文件ID,文件格式类型,文件内容类型,文件上传时间,上传用户ID,文件存储路径,标题文本,章节文本,父段文本,子段文本,标题向量,章节向量,父段向量,子段向量,版次,关键词,字符数")
                .append(")")
                .append("values")
                .append("(")
                .append("\"").append(personalVector.getUserId()).append("\"").append(",")
                .append("\"").append(personalVector.getUserName()).append("\"").append(",")
                .append("\"").append(personalVector.getCompanyCode()).append("\"").append(",")
                .append("\"").append(personalVector.getCompanyName()).append("\"").append(",")
                .append("\"").append(personalVector.getDepartmentName()).append("\"").append(",")
                .append("\"").append(personalVector.getFileName()).append("\"").append(",")
                .append("\"").append(personalVector.getFileId()).append("\"").append(",")
                .append("\"").append(personalVector.getFileType()).append("\"").append(",")
                .append("\"").append(personalVector.getFileContentType()).append("\"").append(",")
                .append("\"").append(personalVector.getUploadTime()).append("\"").append(",")
                .append("\"").append(personalVector.getUploadUserId()).append("\"").append(",")
                .append("\"").append(personalVector.getFilePath()).append("\"").append(",")
                .append("\"").append(personalVector.getTitle()).append("\"").append(",")
                .append("\"").append(personalVector.getChapter()).append("\"").append(",")
                .append("\"").append(personalVector.getSliceParent()).append("\"").append(",")
                .append("\"").append(personalVector.getSliceChild()).append("\"").append(",")
                .append("\"").append(personalVector.getTitleVector()).append("\"").append(",")
                .append("\"").append(personalVector.getChapterVector()).append("\"").append(",")
                .append("\"").append(personalVector.getSliceParentVector()).append("\"").append(",")
                .append("\"").append(personalVector.getSliceChildVector()).append("\"").append(",")
                .append("\"").append(personalVector.getEdition()).append("\"").append(",")
                .append("\"").append(personalVector.getKeywords()).append("\"").append(",")
                .append("\"").append(personalVector.getWordCount()).append("\"")
                .append(")");
        return insertSql.toString();
    }
}
