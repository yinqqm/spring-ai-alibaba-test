package com.ai.demo;

import com.ai.demo.tool.CreateChatClient;
import org.junit.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;

public class ModelsTester {

    /**
     * 演示ChatClient
     * 简单对话: 聊天机器人、FAQ系统
     * 文本生成: 文章写作、翻译、摘要
     * 单次工具调用: 简单的查询操作
     * RAG检索: 结合向量数据库的问答
     * 流式输出: 实时打字机效果
     */
    @Test
    public void test1(){
        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        ChatClient.CallResponseSpec responseSpec = ChatClient.create(chatModel)
                .prompt("你是谁")
                .call();
        String content = responseSpec.content();
        System.out.println(content);
    }
}
