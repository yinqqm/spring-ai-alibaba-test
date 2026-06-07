package com.ai.demo;

import com.ai.demo.tool.CreateChatClient;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

/**
 * function tool，实现了实时的天气查询通过大模型调用function
 */
public class WeatherAgent {
    public static void main(String[] args) {
        ChatModel chatModel = CreateChatClient.createDefaultOllamaChatModel();
        ToolCallback weatherTool = FunctionToolCallback.builder("get_weather", new WeatherTool())
                .description("Get weather for a given city")
                .inputType(WeatherToolRequest.class)
                .build();

        // 创建 agent
        ReactAgent agent = ReactAgent.builder()
                .name("weather_agent")
                .model(chatModel)
                .tools(weatherTool)
                .systemPrompt("You are a helpful assistant")
                .saver(new MemorySaver())
                .build();

        // 运行 agent
        AssistantMessage response = null;
        try {
            response = agent.call("what is the weather in 广州");
        } catch (GraphRunnerException e) {
            throw new RuntimeException(e);
        }
        System.out.println(response.getText());

    }

}
