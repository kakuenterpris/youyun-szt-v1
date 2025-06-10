package com.ustack.file.enums;


import com.ustack.global.common.rest.ErrorCode;
import lombok.Getter;

/**
 * @author linxin
 * @Description : 接口响应错误码
 * @ClassName : ErrorCodeEnum
 * @Date: 2022-06-20 13:38
 */
public enum ErrorCodeEnum implements ErrorCode {

    FILE_NOT_EXIST(2000, "文件不存在"),
    FILE_CANT_PREVIEW(2001, "文件类型不支持预览"),
    FILE_CANT_TO_PDF(2002, "文件类型不支持转PDF"),
    FILE_CANT_ADD_STAMP(2003, "文件类型不支持加水印"),
    FILE_CANT_INSERT_IMAGE(2004, "文件类型不支持插入图片"),
    INSERT_IMAGE_TYPE_WRONG(2005, "插入图片文件类型错误，suffix = %s"),
    TEMPLATE_FILE_NOT_EXIST(2006, "模板文件不存在"),
    FILE_CANT_MAIL_MERGE(2007, "模板文件格式错误，不支持邮件合并操作"),
    FILE_RECORDS_NOT_EXIST(2008, "未找到对应文件记录"),
    BIZ_ID_FILE_GUID_ISNULL(2009,"业务ID和文件ID不可同时为空"),
    UPDATE_ENABLE_DOWNLOAD_ERROR(2010, "修改文件是否可下载失败"),
    QUERY_FILE_PARAMS_IS_NULL(2011, "查询文件时参数不能全为空"),
    MERGE_FILE_LIST_IS_NULL(2012, "文件合并列表为空"),
    EXCEL_EXPORT_TEMPLATE_NOT_EXIST(2013, "Excel导出模板不存在"),
    COMPRESS_FILES_NOT_EXIST(2014, "压缩文件不存在"),
    MERGE_FILE_FAILED(2015, "文件合并失败"),
    FILE_CANT_READ_INVOICE(2016, "文件类型不支持发票识别"),
    FILE_FORMAT_CANT_READ_INVOICE(2017, "发票读取失败，请使用原生PDF文件"),
    REQUIRE_PARAMS_ISNULL(2018, "必传参数为空"),
    FILE_SUFFIX_REQUIRED(2019, "文件后缀不能为空"),
    BATCH_QUERY_TYPE_ISNULL(2020, "批量查询类型不能为空"),
    BATCH_QUERY_TYPE_INVALID(2021, "批量查询类型无效（10 根据文件guid查询、20 根据业务guid查询）"),
    COPY_PARAM_ISNULL(2022, "复制参数不能为空！"),
    FILE_UPLOAD_FAILED(2023, "文件上传失败！"),
    FILE_RENAME_FAILED(2024, "文件重命名失败！"),
    DIRECTORY_NOT_EXISTS(2025, "文件夹不存在！"),
    FILE_NAME_CONFLICT(2026, "移动后的文件夹中有同名文件，请重新命名！"),
    FILE_TYPE_INVALID(2027, "文件类型不支持！"),
    NEW_DOC_IS_EMPTY(2028, "新文档内容读取为空！"),
    OLD_DOC_IS_EMPTY(2029, "旧文档内容读取为空！"),
    DOC_READ_FAILED(2030, "文档内容读取失败！"),
    INVOICE_READ_WRONG(2031, "发票读取错误！"),

    EXCEL_EXPORT_FAILED(2034, "数据导出失败"),
    FILE_TYPE_MUST_BE_DOC(2035, "文件类型不能作为填充模板"),

    FILL_DOC_TEMPLATE_FAIL(2036, "填充doc模板文件失败"),


    request_youyun_failed(3000, "请求有云接口失败！"),
    convert_to_record_failed(3001, "返回数据转换失败！"),


    ;

    @Getter
    private Integer code;

    @Getter
    private String msg;



    ErrorCodeEnum(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }


    @Override
    public String getMsg() {
        return this.msg;
    }

    @Override
    public Integer getCode() {
        return this.code;
    }

}
