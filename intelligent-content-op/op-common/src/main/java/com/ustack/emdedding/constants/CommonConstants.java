package com.ustack.emdedding.constants;

import okhttp3.MediaType;
import org.apache.commons.math3.complex.ComplexFormat;

import java.text.SimpleDateFormat;

/**
 * @author zhangwei
 * @date 2025年04月09日
 */
public class CommonConstants {
    public static final SimpleDateFormat outputFormat = new SimpleDateFormat("yyyy-MM-dd");
    public static final Integer YEAR_ADD = 1900;

    public static final MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json");
    public static final String CHUNKS_TITLE_PARAM = "title";
    public static final String CHUNKS_SECTION_PARAM = "section";
    public static final String CHUNKS_PARENT_PARAM = "parentContent";
    public static final String CHUNKS_CHILD_PARAM = "childContent";
    public static final String CHUNKS_EDITION_PARAM = "edition";
    public static final Integer CHUNKS_PAGE = 1;
    public static final Integer CHUNKS_PAGE_SIZE = 100;
    public static final String REDIS_CHUNKS_KEY = "rag:process:documentId:";
    public static final String split_semicolon = ";";

    // 相关度最小取值
    public static final Double RELEVANCE_MIN = 30.0;

    public static final int MAX_EMBEDDING_LENGTH = 8000;
}
