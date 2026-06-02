package com.ai.demo;

import com.ai.demo.agent.*;
import com.ai.demo.tool.CreateChatClient;
import com.alibaba.cloud.ai.dashscope.api.DashScopeApi;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatOptions;
import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.agent.hook.modelcalllimit.ModelCallLimitHook;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.alibaba.cloud.ai.graph.utils.Messageutils;
import org.junit.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.converter.BeanOutputConverter;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.function.FunctionToolCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class AgentsTester {
    /**---------------------------高级特性-------------------***/
    /**
     * 通过Hook来实现模型调用次数的控制
      */
    @Test
    public void test19() throws GraphRunnerException {
        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        ToolCallback chunkTool = FunctionToolCallback.builder("chunkTool", new ChunkTool())
                .description("根据索引返回一段内容，方便agent 通过多轮 tool 调用逐步获得所有内容")
                .inputType(SearchToolInput.class)
                .build();
        ModelLoggingHook modelLoggingHook = new ModelLoggingHook();
        ReactAgent modelHook = ReactAgent.builder()
                .name("model_hook")
                .model(chatModel)
                .tools(chunkTool)
                //配置一次agent call最多只能调用7次LLM
                .hooks(ModelCallLimitHook.builder().runLimit(7).build(),modelLoggingHook)
                .build();
        AssistantMessage message = modelHook.call("""
                请严格按照顺序完成：
                1.依次调用chunkTool的apply方法，从SearchToolInput的query属性的值从0到7.
                2.每次只处理一段，不要跳步.
                3.最后用中文总结所有内容.
                """);
        System.out.println(message.getText());

    }


    /**
     * Hook测试之ModelHook的测试
     * 一次agent call 多次调用模型
     */
    @Test
    public void test18() throws GraphRunnerException {
        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        ToolCallback chunkTool = FunctionToolCallback.builder("chunkTool", new ChunkTool())
                .description("根据索引返回一段内容，方便agent 通过多轮 tool 调用逐步获得所有内容")
                .inputType(SearchToolInput.class)
                .build();
        //创建ModelHooks
        MessageTrimmingHook messageTrimmingHook = new MessageTrimmingHook();
        LoggingHook loggingHook = new LoggingHook();
        ReactAgent modelHook = ReactAgent.builder()
                .name("model_hook")
                .model(chatModel)
                .tools(chunkTool)
                .hooks(messageTrimmingHook,loggingHook)
                .build();
        AssistantMessage message = modelHook.call("""
                请严格按照顺序完成：
                1.依次调用chunkTool的apply方法，从SearchToolInput的query属性的值从0到7.
                2.每次只处理一段，不要跳步.
                3.最后用中文总结所有内容.
                """);
        System.out.println(message.getText());
    }



    /**
     * Hook测试之AgentHook的测试
     */
    @Test
    public void test17() throws GraphRunnerException {
        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        LoggingHook loggingHook = new LoggingHook();
        ReactAgent agentHook = ReactAgent.builder()
                .name("agent_hook")
                .model(chatModel)
                .hooks(loggingHook)
                .build();
        AssistantMessage message = agentHook.call("你是谁?");
        System.out.println(message.getText());
        agentHook.call("你的作用是什么");

    }


    /**
     * memory，测试环境可以使用Memory saver
     * 但是生产环境推荐使用：RedisSaver、MongoSaver等持久化的Saver
     */
    @Test
    public void test16() throws GraphRunnerException {
        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        // 配置内存存储
        ReactAgent agent = ReactAgent.builder()
                .name("memory_agent")
                .model(chatModel)
                .saver(new MemorySaver())
                .build();

// 使用 thread_id 维护对话上下文
        RunnableConfig config = RunnableConfig.builder()
                .threadId("user_123")
                .build();

        agent.call("我叫张三", config);
        AssistantMessage message = agent.call("我叫什么名字？", config);// 输出: "你叫张三"
        System.out.println(message.getText());
    }


    /**
     * 通过outputSchema 格式化输出，通过自定义outputSchema方式
     * @throws GraphRunnerException
     */
    @Test
    public void test15() throws GraphRunnerException {
        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        BeanOutputConverter<TextAnalysisResult> outputConverter = new BeanOutputConverter<>(TextAnalysisResult.class);
        String jsonSchema = outputConverter.getJsonSchema();
        String template = """
				Your response should be in JSON format.
				Do not include any explanations, only provide a RFC8259 compliant JSON response following this format without deviation.
				Do not include markdown code blocks in your response.
				Remove the ```json markdown from the output.
				Here is the JSON Schema instance your output and you must remove spentTimeSeconds properties:
				```%s```
				""";
        String format = String.format(template, jsonSchema);

        ReactAgent agent = ReactAgent.builder()
                .name("output_schema")
                .model(chatModel)
                .outputSchema(format)
                .build();

        AssistantMessage message = agent.call("分析这段文本：这个鞋子很舒服，不错。");
        System.out.println(message.getText());
    }


    /**
     * 通过outputSchema 格式化输出
     * @throws GraphRunnerException
     */
    @Test
    public void test14() throws GraphRunnerException {
        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        BeanOutputConverter<TextAnalysisResult> outputConverter = new BeanOutputConverter<>(TextAnalysisResult.class);
        String format = outputConverter.getFormat();
        ReactAgent agent = ReactAgent.builder()
                .name("output_schema")
                .model(chatModel)
                .outputSchema(format)
                .build();

        AssistantMessage message = agent.call("分析这段文本：这个鞋子很舒服，不错。");
        System.out.println(message.getText());
    }



    /**
     * 结构化输出，通过outputType的方式来进行格式化输出
     * @throws GraphRunnerException
     */
    @Test
    public void test13() throws GraphRunnerException {
        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();

        ReactAgent agent = ReactAgent.builder()
                .name("format_agent_output_type")
                .model(chatModel)
                .outputType(PoemOutput.class)
                .build();

        AssistantMessage message = agent.call("编写一篇描述夏天的散文");
        //输出会遵循PoemOutput的结构
        System.out.println(message.getText());
    }




    /**---------------------------agent 的调用-------------------***/


    /**
     * metaData之Tool的使用
     * @throws GraphRunnerException
     */
    @Test
    public void test11() throws GraphRunnerException {

        //创建tool 类
        ToolCallback searchTool = FunctionToolCallback.
                builder("search", new SearchTool()).description("通过给定的参数查询线上新闻并返回结果") //定义工具描述，提供给模型的使用指南
                .inputType(SearchToolInput.class).build();

        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        ReactAgent metaDataTool = ReactAgent.builder()
                .name("meta_data_tool")
                .model(chatModel)
                .tools(searchTool)
                .build();

        //创建RunnableConfig
        RunnableConfig runnableConfig = RunnableConfig.builder()
                .addMetadata("call_tool", "yes")
                .build();

        AssistantMessage assistantMessage = metaDataTool.call("今天发生了什么新闻", runnableConfig);

        System.out.println(assistantMessage.getText());

    }


    /**
     * metaData之Interceptor的使用
     * @throws GraphRunnerException
     */
    @Test
    public void test10() throws GraphRunnerException {

        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        ReactAgent agent = ReactAgent.builder()
                .name("model_interceptor_agent")
                .model(chatModel)
                .interceptors(new DynamicPromptInterceptor()).build();
        //通过runnableConfig指定配置信息
        RunnableConfig runnableConfig = RunnableConfig.builder().addMetadata("user_role", "expert").build();
        AssistantMessage assistantMessage = agent.call("介绍一下Spring boot的自动注入原理。", runnableConfig);
        System.out.println(assistantMessage.getText());
    }

    /**
     * 使用配置,测试threadId实现不同用户之间使用不同的上下文信息
     */
    @Test
    public void test9() throws GraphRunnerException {
        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        ReactAgent agent = ReactAgent.builder()
                .name("runnable_config")
                .model(chatModel)
                .saver(new MemorySaver())
                .build();
        //通过RunnableConfig 传递运行时配置
        String threadId123 = "thread_123";
        String threadId456 = "thread_456";
        RunnableConfig runnableConfig123 = RunnableConfig.builder()
                .threadId(threadId123)
                .build();
        RunnableConfig runnableConfig456 = RunnableConfig.builder()
                .threadId(threadId456)
                .build();
        agent.call("我叫123", runnableConfig123);
        agent.call("我叫456", runnableConfig456);


        AssistantMessage response123 = agent.call("我叫什么？", runnableConfig123);
        System.out.println("runnableConfig123:" + response123.getText());
        AssistantMessage response456 = agent.call("我叫什么？", runnableConfig456);
        System.out.println("runnableConfig456:" + response456.getText());

    }


    /**
     * 获取完整状态
     * 获取自定义的值
     *
     * @throws GraphRunnerException
     */
    @Test
    public void test8() throws GraphRunnerException {
        // ★ 关键：通过 Map 传入自定义状态（messages/input 之外的都是自定义状态）
        Map<String, Object> inputs = new HashMap<>();
        String inputMessage = "帮我写一首诗";
        inputs.put("input", inputMessage);           // 预留关键字：用户输入
        inputs.put("messages", Messageutils.convertToMessages(inputMessage));
        inputs.put("custom_key", "这是我在test8中设置的值");   // ★ 自定义状态
        inputs.put("user_role", "admin");              // ★ 自定义状态
        inputs.put("session_id", "sess_001");          // ★ 自定义状态
        inputs.put("request_count", 0);                // ★ 自定义状态

        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        ReactAgent getAllState = ReactAgent.builder()
                .model(chatModel)
                .name("get_all_state")
               // .instruction("你是一个助手，请根据用户输入完成任务。")
                .build();
        Optional<OverAllState> result =  getAllState.invoke(inputs);
        if (result.isPresent()) {
            OverAllState overAllState = result.get();
            Optional<Object> messages = overAllState.value("messages");
            // 访问自定义状态
            Optional<Object> customData = overAllState.value("custom_key");
            if (customData.isPresent()) {
                System.out.println("获取设置的custom_key:" + customData);
            }
            System.out.println("完整状态：" + overAllState);
        }
    }

    /**
     * ------------------------System Prompt系统提示词----------------
     ***/
    @Test
    public void test7() throws GraphRunnerException {
        //通过instruction 指定更加详细的指令
        String instruction = """
                你是一个经验丰富的软件架构师。
                
                在回答问题时，请：
                1. 首先理解用户的核心需求
                2. 分析可能的技术方案
                3. 提供清晰的建议和理由
                4. 如果需要更多信息，主动询问
                
                保持专业、友好的语气。
                """;
        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        ReactAgent agent = ReactAgent.builder().name("architect_agent").model(chatModel).instruction(instruction).build();

        AssistantMessage message = agent.call("设计一个答题系统");
        System.out.println(message.getText());
    }

    /**
     * 工具错误处理
     *
     * @throws GraphRunnerException
     */
    @Test
    public void test6() throws GraphRunnerException {
        //创建tool 类
        ToolCallback searchTool = FunctionToolCallback.builder("search", new SearchTool()).description("通过给定的参数查询线上新闻并返回结果") //定义工具描述，提供给模型的使用指南
                .inputType(SearchToolInput.class).build();

        //通过SystemPrompt方法控制简单的系统提示词
        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        ReactAgent searchAgent = ReactAgent.builder().model(chatModel) //设置model
                .name("search_agent") //设置agent的名称
                .tools(searchTool).interceptors(new ToolErrorInterceptor()).systemPrompt("调用工具输出什么，模型就输出什么，不能进行修改、添加、删除。").build();


        AssistantMessage message = searchAgent.call("今天的新闻有哪些?");
        System.out.println(message.getText());
    }


    /** ------------------------agent的核心组件Tools和Interceptor----------------***/

    /**
     * 模型拦截器示例
     */
    @Test
    public void test5() throws GraphRunnerException {
        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        ReactAgent agent = ReactAgent.builder().name("model_interceptor_agent").model(chatModel).interceptors(new DynamicPromptInterceptor()).build();
        //通过runnableConfig指定配置信息
        RunnableConfig runnableConfig = RunnableConfig.builder().addMetadata("user_role", "expert").build();
        AssistantMessage assistantMessage = agent.call("介绍一下Spring boot的自动注入原理。", runnableConfig);
        System.out.println(assistantMessage.getText());
    }


    /**
     * 工具拦截器示例
     */
    @Test
    public void test4() throws GraphRunnerException {
        //创建tool 类
        ToolCallback searchTool = FunctionToolCallback.builder("search", new SearchTool()).description("通过给定的参数查询线上新闻并返回结果") //定义工具描述，提供给模型的使用指南
                .inputType(SearchToolInput.class).build();

        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
//        //创建Agent
        ReactAgent searchAgent = ReactAgent.builder().model(chatModel) //设置model
                .name("search_agent") //设置agent的名称
                .tools(searchTool).interceptors(new ToolErrorInterceptor()).systemPrompt("调用工具输出什么，模型就输出什么，不能进行修改、添加、删除。").build();
        AssistantMessage assistantMessage = searchAgent.call("请搜索昨天发生了哪些新闻？");
        System.out.println("结果是：" + assistantMessage.getText());
    }


    /**
     * 工具调用示例
     */
    @Test
    public void test3() throws GraphRunnerException {
        //创建tool 类
        ToolCallback searchTool = FunctionToolCallback.builder("search", new SearchTool()).description("通过给定的参数查询线上新闻并返回结果") //定义工具描述，提供给模型的使用指南
                .inputType(SearchToolInput.class).build();

        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
//        //创建Agent
        ReactAgent searchAgent = ReactAgent.builder().model(chatModel) //设置model
                .name("search_agent") //设置agent的名称
                .tools(searchTool).build();
        AssistantMessage assistantMessage = searchAgent.call("请搜索今天发生了哪些新闻？");
        System.out.println("结果是：" + assistantMessage.getText());
    }

    /** ------------------------agent的核心组件 模型----------------***/
    /**
     * 高级模型配置
     */
    @Test
    public void test2() throws GraphRunnerException {
        //创建ChatModel
        String apiKey = System.getenv("AI_DASHSCOPE_API_KEY");
        // 创建DashScopeApi实例
        DashScopeApi dashScopeApi = DashScopeApi.builder().apiKey(apiKey).build();
        //创建ChatModel
        ChatModel chatModel = DashScopeChatModel.builder().dashScopeApi(dashScopeApi).defaultOptions(DashScopeChatOptions.builder().model(DashScopeChatModel.DEFAULT_MODEL_NAME)//模型名称
                .temperature(0.7) //控制随机性
                .maxToken(2000) //最大输出长度
                .topP(0.9) //核采样，控制输出的多样性
                .topK(50) //采样候选池的大小（top-k）。例如，topk = 50 表示只考虑得分最高的 50 个标记进行随机采样。较大的值增加随机性；较小的值增加确定性。
                .build()).build();
        //基于模型，指定agent的名称，创建agent
        ReactAgent simpleModelAgent = ReactAgent.builder().model(chatModel).name("simple_model_agent").build();
    }


    /**
     * 基本模型配置
     */
    @Test
    public void test1() {
        //创建ChatModel
        String apiKey = System.getenv("AI_DASHSCOPE_API_KEY");
        // 创建DashScopeApi实例
        DashScopeApi dashScopeApi = DashScopeApi.builder().apiKey(apiKey).build();
        //创建ChatModel
        ChatModel chatModel = DashScopeChatModel.builder().dashScopeApi(dashScopeApi).build();
        //基于模型，指定agent的名称，创建agent
        ReactAgent simpleModelAgent = ReactAgent.builder().model(chatModel).name("simple_model_agent").build();

    }
}
