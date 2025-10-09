
package com.x.ai.config;

import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.EmbeddingStoreIngestor;
import dev.langchain4j.store.embedding.pinecone.PineconeEmbeddingStore;
import dev.langchain4j.store.embedding.pinecone.PineconeServerlessIndexConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EmbeddingStoreConfig {

    @Autowired
    private EmbeddingModel embeddingModel;

    @Bean
    public EmbeddingStore<TextSegment> embeddingStore() {
        // 创建向量存储
        EmbeddingStore<TextSegment> embeddingStore = PineconeEmbeddingStore.builder()
                .apiKey(System.getenv("PINECONE_API_KEY"))
                .index("blog") // 如果指定的索引不存在，将创建一个新的索引
                .nameSpace("myBlog") // 如果指定的名称空间不存在，将创建一个新的名称空间
                .createIndex(PineconeServerlessIndexConfig.builder()
                        .cloud("AWS") // 指定索引部署在 AWS 云服务上
                        .region("us-east-1") // 指定索引所在的 AWS 区域为 us-east-1
                        .dimension(embeddingModel.dimension()) // 指定索引的向量维度
                        .build())
                .build();

        // 加载原始文档并清理Markdown格式
        Document originalDocument = FileSystemDocumentLoader.loadDocument("/Users/x/projects/BMS/backend/src/main/resources/rag.md");
        Document cleanedDocument = cleanMarkdownDocument(originalDocument);

        // 使用清理后的文档进行向量化
        EmbeddingStoreIngestor
                .builder()
                .embeddingStore(embeddingStore)
                .embeddingModel(embeddingModel)
                .build()
                .ingest(cleanedDocument);

        return embeddingStore;
    }

    // Markdown文档清理方法
    private Document cleanMarkdownDocument(Document document) {
        String cleanedContent = cleanMarkdownContent(document.text());
        return Document.from(cleanedContent, document.metadata());
    }

    // Markdown内容清理方法
    private String cleanMarkdownContent(String markdownContent) {
        if (markdownContent == null || markdownContent.trim().isEmpty()) {
            return markdownContent;
        }

        return markdownContent
                .replaceAll("#{1,6}\\s*", "") // 移除标题标记
                .replaceAll("\\*\\*\\*([^*]+)\\*\\*\\*", "$1") // 移除粗斜体
                .replaceAll("\\*\\*([^*]+)\\*\\*", "$1") // 移除加粗
                .replaceAll("\\*([^*]+)\\*", "$1") // 移除斜体
                .replaceAll("`{3}[\\s\\S]*?`{3}", "") // 移除代码块
                .replaceAll("`([^`]+)`", "$1") // 移除行内代码
                .replaceAll("\\[([^\\]]+)\\]\\([^)]+\\)", "$1") // 移除链接，保留文字
                .replaceAll("!\\[([^\\]]*)\\]\\([^)]+\\)", "$1") // 移除图片，保留alt文字
                .replaceAll("[-*+]\\s+", "") // 移除无序列表标记
                .replaceAll("\\d+\\.\\s+", "") // 移除有序列表标记
                .replaceAll(">\\s*", "") // 移除引用标记
                .replaceAll("\\|?\\s*\\|-*\\|\\s*\\|?", " ") // 简化表格
                .replaceAll("\\~{2}([^~]+)\\~{2}", "$1") // 移除删除线
                .replaceAll("\\s*\\n{3,}\\s*", "\n\n") // 将多个换行合并为两个
                .replaceAll("\\s+", " ") // 合并多余空格
                .trim();
    }
}