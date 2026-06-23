package com.ai.demo;

import com.ai.demo.agent.SearchTool;
import com.ai.demo.agent.SearchToolInput;
import com.ai.demo.tool.CreateChatClient;
import com.ai.demo.tools.CustomToolCallResultConverter;
import com.ai.demo.tools.CustomerTools;
import com.ai.demo.tools.DateTimeTools;
import com.ai.demo.tools.WeatherTools;
import org.junit.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.function.FunctionToolCallback;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.ai.tool.method.MethodToolCallback;
import org.springframework.ai.tool.support.ToolDefinitions;
import org.springframework.ai.tool.support.ToolUtils;
import org.springframework.ai.util.json.schema.JsonSchemaGenerator;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Method;

public class ToolsTester {

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
                .toolMethod(method)
                .build();

        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();

        String content = ChatClient.create(chatModel)
                .prompt("What's the weather of beijing?")
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
