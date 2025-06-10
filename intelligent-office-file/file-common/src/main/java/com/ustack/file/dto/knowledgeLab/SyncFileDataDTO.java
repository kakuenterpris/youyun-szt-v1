package com.ustack.file.dto.knowledgeLab;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


/**
 * @Description: TODO
 * @author：linxin
 * @ClassName: SyncFileDTO
 * @Date: 2025-02-20 14:43
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SyncFileDataDTO {

    /**
     * 源文档 ID 用于重新上传文档或修改文档清洗、分段配置，缺失的信息从源文档复制
     */
    private String original_document_id ;
    /**
     * 索引方式:
     * high_quality 高质量：使用 embedding 模型进行嵌入，构建为向量数据库索引
     * economy 经济：使用 keyword table index 的倒排索引进行构建
     */
    private String indexing_technique;


    /**
     * 索引内容的形式:
     * text_model text 文档直接 embedding，经济模式默认为该模式
     * hierarchical_model parent-child 模式
     * qa_model Q&A 模式：为分片文档生成 Q&A 对，然后对问题进行 embedding
     */
    private String doc_form;


    /**
     * 文档类型（选填）:
     * book 图书 文档记录一本书籍或出版物
     * web_page 网页 网页内容的文档记录
     * paper 学术论文/文章 学术论文或研究文章的记录
     * social_media_post 社交媒体帖子 社交媒体上的帖子内容
     * wikipedia_entry 维基百科条目 维基百科的词条内容
     * personal_document 个人文档 个人相关的文档记录
     * business_document 商业文档 商业相关的文档记录
     * im_chat_log 即时通讯记录 即时通讯的聊天记录
     * synced_from_notion Notion同步文档 从Notion同步的文档内容
     * synced_from_github GitHub同步文档 从GitHub同步的文档内容
     * others 其他文档类型 其他未列出的文档类型
     */
    private String doc_type;


    /**
     * 文档元数据（如提供文档类型则必填 字段因文档类型而异
     *
     * 针对图书类型 For book:
     *
     * title 书名 书籍的标题
     * language 图书语言 书籍的语言
     * author 作者 书籍的作者
     * publisher 出版社 出版社的名称
     * publication_date 出版日期 书籍的出版日期
     * isbn ISBN号码 书籍的ISBN编号
     * category 图书分类 书籍的分类类别
     * 针对网页类型 For web_page:
     *
     * title 页面标题 网页的标题
     * url 页面网址 网页的URL地址
     * language 页面语言 网页的语言
     * publish_date 发布日期 网页的发布日期
     * author/publisher 作者/发布者 网页的作者或发布者
     * topic/keywords 主题/关键词 网页的主题或关键词
     * description 页面描述 网页的描述信息
     * 请查看 https://github.com/langgenius/dify/blob/main/api/services/dataset_service.py#L475了解各文档类型所需字段的详细信息。
     * 针对"其他"类型文档，接受任何有效的JSON对象
     */
    private String doc_metadata;


    /**
     * 在 Q&A 模式下，指定文档的语言，例如：English、Chinese
     */
    private String doc_language;


    /**
     * process_rule 处理规则
     *
     * mode (string) 清洗、分段模式 ，automatic 自动 / custom 自定义
     * rules (object) 自定义规则（自动模式下，该字段为空）
     *   pre_processing_rules (array[object]) 预处理规则
     *     id (string) 预处理规则的唯一标识符
     *      枚举：remove_extra_spaces 替换连续空格、换行符、制表符
     *           remove_urls_emails 删除 URL、电子邮件地址;
     *     enabled (bool) 是否选中该规则，不传入文档 ID 时代表默认值;
     *   segmentation (object) 分段规则
     *     separator 自定义分段标识符，目前仅允许设置一个分隔符。默认为 \n
     *     max_tokens 最大长度（token）默认为 1000
     *   parent_mode 父分段的召回模式 full-doc 全文召回 / paragraph 段落召回
     *   subchunk_segmentation (object) 子分段规则
     *     separator 分段标识符，目前仅允许设置一个分隔符。默认为 ***
     *     max_tokens 最大长度 (token) 需要校验小于父级的长度
     *     chunk_overlap 分段重叠指的是在对数据进行分段时，段与段之间存在一定的重叠部分（选填）
     */
    private ProcessRule process_rule;

}
