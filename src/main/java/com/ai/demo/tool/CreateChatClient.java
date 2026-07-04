package com.ai.demo.tool;

import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import org.apache.commons.lang3.StringUtils;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaApi;
import org.springframework.ai.ollama.api.OllamaChatOptions;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.ai.openai.api.OpenAiApi;

public final class CreateChatClient {

    /**
     * 创建Ollama的ChatModel
     *
     * @return
     */
    public static ChatModel createDefaultOllamaChatModel() {
        return createOllamaChatModel(null);
    }

    /**
     * 创建Ollama的ChatModel
     *
     * @return
     */
    public static ChatModel createOllamaChatModel(String modelName) {
        if (StringUtils.isBlank(modelName)) {
            modelName = "qwen3.5:2b";
        }
        OllamaApi ollamaApi = OllamaApi.builder().baseUrl("http://127.0.0.1:11434").build();
        ChatModel chatModel = OllamaChatModel.builder().
                ollamaApi(ollamaApi).
                defaultOptions(OllamaChatOptions.builder().model(modelName)
                        .build()).build();
        return chatModel;
    }


    public static ChatModel createOpenAIModel() {
        OpenAiApi openAiAp = OpenAiApi.builder()
                .apiKey("your deepseek token")
                .baseUrl("https://api.deepseek.com")
                .build();
        OpenAiChatModel chatModel = OpenAiChatModel.builder()
                .openAiApi(openAiAp)
                .defaultOptions(OpenAiChatOptions.builder()
                        .model("deepseek-v4-flash")
                        .temperature(0.7)
                        .build())
                .build();
        return chatModel;
    }


    /**
     * 创建DashScope的ChatModel
     *
     * @return
     */
    public static ChatModel createDashScopeChatModel() {
        String apiKey = System.getenv("AI_DASHSCOPE_API_KEY");
        // 创建模型实例
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(apiKey)
                .build();
        ChatModel chatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                //设置option是否执行Tooling Call
//                .defaultOptions(
//                        DashScopeChatOptions.builder()
//                        .internalToolExecutionEnabled(Boolean.FALSE)
//                        .model("qwen-plus")
//                        .build())
                .build();
        return chatModel;
    }


    /**
     * 创建DashScope的ChatModel
     *
     * @return
     */
    public static ChatModel createDashScopeChatModelOfQWEN3Dot6plus() {
        String apiKey = System.getenv("AI_DASHSCOPE_API_KEY");
// 创建模型实例
        DashScopeApi dashScopeApi = DashScopeApi.builder()
                .apiKey(apiKey)
                .build();
        ChatModel chatModel = DashScopeChatModel.builder()
                .dashScopeApi(dashScopeApi)
                .defaultOptions(DashScopeChatOptions.builder()
                        .model("qwen3.6-plus")
                        .multiModel(true)
                        .build())
                .build();
        return chatModel;
    }
}
