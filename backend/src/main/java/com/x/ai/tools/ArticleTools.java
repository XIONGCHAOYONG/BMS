package com.x.ai.tools;


import dev.langchain4j.agent.tool.Tool;
import dev.langchain4j.agent.tool.ToolMemoryId;
import org.springframework.stereotype.Component;

@Component
public class ArticleTools {

    @Tool(name = "getCounts", value = "得到博客文章的数量")
    int getCounts(@ToolMemoryId Long memoryId) {
        return 10;
    }
}
