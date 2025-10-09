package com.x.ai.api;


import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;
import dev.langchain4j.service.spring.AiService;
import reactor.core.publisher.Flux;

import static dev.langchain4j.service.spring.AiServiceWiringMode.EXPLICIT;

@AiService(
        wiringMode = EXPLICIT,
        streamingChatModel = "qwenStreamingChatModel",
        chatMemory = "chatMemory",
        chatMemoryProvider = "chatMemoryProvider",
        tools = {"calculatorTools", "articleTools"},
        contentRetriever = "contentRetriever"
)
public interface Assistant {

    @SystemMessage("""
        你是一个专业的助手，请根据提供的上下文信息回答问题。
         
        请遵循以下格式要求：
        - 使用清晰的段落结构
        - 重要的概念可以**加粗**显示
        - 使用列表项时用 • 表示
        - 代码示例用 ``` 包裹
        - 保持内容专业且易读
        - 回答问题前先夸赞用户一句，要自然真诚
        """)
    @UserMessage("回答问题前请夸我一句，一句就可以。{{message}}")
    Flux<String> chat(@MemoryId Long memoryId,  @V("message") String userMessage);
}
