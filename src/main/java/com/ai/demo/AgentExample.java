package com.ai.demo;

import com.ai.demo.tool.CreateChatClient;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;

public class AgentExample {
    public static void main(String[] args) {
        // 创建模型实例
        ChatModel chatModel = CreateChatClient.createDefaultOllamaChatModel();
        // 创建 Agent
        ReactAgent agent = ReactAgent.builder()
                .name("weather_agent")
                .model(chatModel)
                .instruction("You are a helpful weather forecast assistant.")
                .build();

        // 运行 Agent
        try {
            AssistantMessage message = agent.call("what is the weather in Hangzhou?");
            System.out.println(message.getText());
        } catch (GraphRunnerException e) {
            throw new RuntimeException(e);
        }
    }
}
