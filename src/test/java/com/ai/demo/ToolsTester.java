package com.ai.demo;

import com.ai.demo.agent.SearchTool;
import com.ai.demo.agent.SearchToolInput;
import com.ai.demo.tool.CreateChatClient;
import com.ai.demo.tools.*;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import org.junit.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.tool.support.ToolDefinitions;
import org.springframework.ai.tool.support.ToolUtils;
import org.springframework.ai.util.json.schema.JsonSchemaGenerator;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;
import java.util.List;

public class ToolsTester {


    /**
     * ToolCallbackProvider 接口动态提供工具
     */
    @Test
    public void test9() throws GraphRunnerException {
        ToolCallback weatherTool = FunctionToolCallback.builder("get weather",
                        new WeatherTool())
                .description("Get weather for a given city")
                .inputType(WeatherToolRequest.class)
                .build();

        ToolCallback searchTool = FunctionToolCallback.builder("search",
                        new SearchTool())
                .description("Search for information")
                .inputType(SearchToolInput.class)
                .build();

        ToolCallbackProvider toolCallbackProvider = new CustomToolCallbackProvider(List.of(weatherTool, searchTool));
        //动态控制有哪些工具
        //ToolCallbackProvider toolCallbackProvider = new CustomToolCallbackProvider(List.of(weatherTool, searchTool),false);

        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        ReactAgent agent = ReactAgent.builder()
                .name("my agent")
                .model(chatModel)
                .toolCallbackProviders(toolCallbackProvider)
                .systemPrompt("You are a helpful assistant with access to weather and search tools.")
                .build();

        AssistantMessage message = agent.call("北京的天气怎么样，今天发生了什么新闻");
        System.out.println(message.getText());

    }

    /**
     * 通过methodTools来设置tool
     */
    @Test
    public void test8() throws GraphRunnerException {
        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();

        ReactAgent agent = ReactAgent.builder()
                .methodTools(new DateTimeTools()) // 通过method tools指定tool
                .name("my agent")
                .model(chatModel)
                .systemPrompt("You are a helpful assistant with date and time")
                .build();

        AssistantMessage message = agent.call("获取当前时间，并设置一个10分钟后的闹钟");
        System.out.println(message.getText());
    }


    /**
     * 通过tools方法直接使用tool
     */
    @Test
    public void test7() throws GraphRunnerException {
        ToolCallback weatherTool = FunctionToolCallback.builder("get weather",
                new WeatherTool())
                .description("Get weather for a given city")
                .inputType(WeatherToolRequest.class)
                .build();

        ToolCallback searchTool = FunctionToolCallback.builder("search",
                 new SearchTool())
                .description("Search for information")
                .inputType(SearchToolInput.class)
                .build();

        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();

        ReactAgent agent = ReactAgent.builder()
                .name("my_agent")
                .tools(weatherTool,searchTool)
                .model(chatModel)
                .systemPrompt("You are a helpful assistant with access to weather and search tools.")
                .saver(new MemorySaver())
                .build();
        AssistantMessage message = agent.call("北京的天气怎么样");
        System.out.println(message.getText());
    }


    /**********ReactAgent中使用工具************/



    /**
     * 编程式Tool Call 自定义结果转换器
     */
    @Test
    public void test6(){
        Method method = ReflectionUtils.findMethod(CustomerTools.class, "getCustomerInfo", Long.class);
        MethodToolCallback customerToolCallback = MethodToolCallback.builder()
                .toolDefinition(
                        ToolDefinition.builder()
                                .description("获取客户信息, 输入参数是用户id，类型是long，必须输入")
                                .inputSchema(JsonSchemaGenerator.generateForMethodInput(method))
                                .name("getCustomerInfo")
                                .build()
                )
                .toolMethod(method)
                .toolObject(new CustomerTools())
                .toolCallResultConverter(new CustomToolCallResultConverter())
//                .toolMetadata(ToolMetadata.builder()
//                        .returnDirect(true)
//                        .build())
                .build();

        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();

        String content = ChatClient.create(chatModel)
                .prompt("请帮忙获取用户id为1的用户信息")
                .toolCallbacks(customerToolCallback)
                .call()
                .content();

        System.out.println("返回的内容："+content);


    }


    /**
     * 方法Tool Call 自定义结果转换器
     */
    @Test
    public void test5(){
        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        String content = ChatClient.create(chatModel)
                .prompt("请帮忙获取用户id为1的用户信息")
                .tools(new CustomerTools())
                .call()
                .content();

        System.out.println("内容:"+content);


    }


    /**
     * FunctionToolCallback 测试
     */
    @Test
    public void test4() {
        ToolCallback searchTool = FunctionToolCallback
                .builder("searching news", new SearchTool())//工具名称
                .description("通过给定的关键字查询线上新闻并返回结果") //描述
                .inputType(SearchToolInput.class)//输入类型
                //.inputSchema(JsonSchemaGenerator.generateForType(SearchToolInput.class)) //自定义设置schema
                .toolMetadata(ToolMetadata.builder()
                        .returnDirect(false)
                        .build()) //设置Tool的元数据信息，这里是否将数据直接返回给客户端
                .build();

        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        String content = ChatClient.create(chatModel)
                .prompt("今天的新闻有哪些?")
                .toolCallbacks(searchTool)
                .call()
                .content();
        System.out.println("结果:" + content);

    }


    /**
     * MethodToolCallBack 测试
     */
    @Test
    public void test3() {
        Method method = ReflectionUtils.findMethod(WeatherTools.class, "getWeatherByCity", String.class);
//        MethodToolCallback methodToolCallback = MethodToolCallback.builder()
//                .toolDefinition(ToolDefinitions.builder(method)
//                        .description("Get the weather and temperature by the city that need to provide.").build())
//                .toolMethod(method)
//                .toolObject(new WeatherTools())
//                .build();
        //第二种方式
        MethodToolCallback methodToolCallback = MethodToolCallback.builder()
                .toolDefinition(ToolDefinition.builder()
                        .description("Get the weather and temperature by the city that need to provide.")
                        .inputSchema(JsonSchemaGenerator.generateForMethodInput(method))
                        .name("getWeatherByCity")
                        .build())
                .toolObject(new WeatherTools())
                .toolMetadata(ToolMetadata.builder().returnDirect(false).build())
                .toolMethod(method)
                .build();

        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();

        String content = ChatClient.create(chatModel)
                .prompt("What's the weather of Beijing?")
                .toolCallbacks(methodToolCallback) //这里要使用toolCallbacks方法来设置工具类
                .call()
                .content();

        System.out.println("结果是:" + content);
    }


    /**
     * 工具调用，模拟设置闹钟
     * 通过设置多个tools来实现
     */
    @Test
    public void test2() {
        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        String alarmContext = ChatClient.create(chatModel)
                .prompt("Can you set an alarm 10 minutes from now?")
                .tools(new DateTimeTools())
                .call()
                .content();

        System.out.println(alarmContext);
    }


    /**
     * 快速开始例子，信息检索
     */
    @Test
    public void test1() {
        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        String dayOfTmr = ChatClient.create(chatModel)
                .prompt("What day is tomorrow?")
                .tools(new DateTimeTools())
                .call()
                .content();

        System.out.println(dayOfTmr);
    }

    /**
     * 快速开始例子，信息检索
     */
    @Test
    public void test0() {
        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        String dayOfTmr = ChatClient.create(chatModel)
                .prompt("你是谁?")
                //.tools(new DateTimeTools())
                .call()
                .content();

        System.out.println(dayOfTmr);
    }
}
