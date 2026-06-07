package com.ai.demo;

import com.ai.demo.tool.CreateChatClient;
import org.junit.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.metadata.ChatResponseMetadata;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.content.Media;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.UrlResource;
import org.springframework.util.MimeTypeUtils;
import reactor.core.publisher.Flux;

import java.net.MalformedURLException;
import java.util.concurrent.CountDownLatch;

/**
 * 测试Message
 */
public class MessageTester {


    /**
     * 流式输出，一一个字或一段话输出，不是输出一段的
     */
    @Test
    public void test9() throws InterruptedException {
        CountDownLatch countDownLatch = new CountDownLatch(1);
        ChatModel dashScopeChatModel = CreateChatClient.createDashScopeChatModel();
        Flux<ChatResponse> responseFlux = dashScopeChatModel.stream(new Prompt("介绍一下机器学习"));
        responseFlux.subscribe(str -> {
                    System.out.println("输出>>>>>>: " + str.getResult().getOutput().getText());
                },
                error -> {
                    System.out.println("出现错误了：" + error);
                },
                () -> {
                    System.out.println("-------------finish------------");
                    countDownLatch.countDown();
                }
        );
        countDownLatch.await();

    }


    /**
     * 获取token的使用
     */
    @Test
    public void test8() {
        ChatModel dashScopeChatModel = CreateChatClient.createDashScopeChatModel();
        ChatResponse chatResponse = dashScopeChatModel.call(new Prompt("你好！你是谁?"));
        ChatResponseMetadata metadata = chatResponse.getMetadata();
        //查看token的使用
        Usage usage = metadata.getUsage();

        System.out.println("Input token:" + usage.getPromptTokens());
        System.out.println("Input token:" + usage.getCompletionTokens());
        System.out.println("Total token:" + usage.getTotalTokens());
    }

    /** ------------------------Assistant Message 表示模型的输出----------------***/

    /**
     * 提供商对消息类型的权重/上下文化方式不同，
     * 这意味着有时手动创建新的 AssistantMessage 对象并将其插入消息历史中（就像它来自模型一样）会很有帮助。
     */
    @Test
    public void test7() {
        AssistantMessage assistantMessage = null;
        //使用builder 模式构建
        assistantMessage = AssistantMessage.builder().content("我很乐意帮助你回答这个问题！").build();
        assistantMessage = new AssistantMessage("我很乐意帮助你回答这个问题！");

        ChatModel dashScopeChatModel = CreateChatClient.createDashScopeChatModel();

        Prompt prompt = Prompt.builder().messages(
                SystemMessage.builder().text("你是一帮助小能手").build(),
                new UserMessage("你能帮助我吗?"),
                assistantMessage,
                UserMessage.builder().text("太好了！ 3+3等于多少").build()
        ).build();

        ChatResponse chatResponse = dashScopeChatModel.call(prompt);
        AssistantMessage outputAssistantMessage = chatResponse.getResult().getOutput();
        System.out.println("outputAssistantMessage" + outputAssistantMessage);
    }


    @Test
    public void test6() {
        ChatModel dashScopeChatModel = CreateChatClient.createDashScopeChatModel();
        ChatResponse chatResponse = dashScopeChatModel.call(new Prompt("你是谁?"));
        AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
        //assistantMessage:AssistantMessage [
        // messageType=ASSISTANT,
        // toolCalls=[],
        // media =[],
        // textContent=你好！我是通义千问（Qwen....，欢迎随时告诉我！😊,
        // metadata={finishReason=STOP, search_info=, role=ASSISTANT, id=381b25f5-88a3-9ff0-b325-8f5127a4400d, messageType=ASSISTANT, reasoningContent=}]
        System.out.println("assistantMessage:" + assistantMessage);
    }


    /** ------------------------UserMessage ----------------***/
    /**
     * 多态内容，远程图片
     * UserMessage 表示用户输入和交互。它们可以包含文本、图像、音频、文件和任何其他数量的多模态内容。
     */
    @Test
    public void test5() throws MalformedURLException {
        //从远程图片读取
        //测试图像
        ChatModel dashScopeChatModel = CreateChatClient.createDashScopeChatModelOfQWEN3Dot5plus();
        //创建一个图像类型的UserMessage
        UserMessage imageUserMessage = UserMessage.builder()
                .text("描述这张图片的内容。")
                .media(Media.builder()
                        .mimeType(MimeTypeUtils.IMAGE_PNG)
                        .data(new UrlResource("https://help-static-aliyun-doc.aliyuncs.com/assets/img/zh-CN/9373463771/p1058343.png"))
                        .build())
                .build();
        //我们直接获取模型响应结果，不使用assistantMessage进行包装
        String output = dashScopeChatModel.call(imageUserMessage);
        System.out.println(output);
    }

    /**
     * 多态内容，本地图片
     * UserMessage 表示用户输入和交互。它们可以包含文本、图像、音频、文件和任何其他数量的多模态内容。
     */
    @Test
    public void test4() throws MalformedURLException {
        //从本地图片读取
        String imagePath = "images/dog.jpg";
        //测试图像
        ChatModel dashScopeChatModel = CreateChatClient.createDashScopeChatModelOfQWEN3Dot5plus();
        //创建一个图像类型的UserMessage
        UserMessage imageUserMessage = UserMessage.builder()
                .text("描述这张图片的内容。")
                .media(Media.builder()
                        .mimeType(MimeTypeUtils.IMAGE_JPEG)
                        .data(new ClassPathResource(imagePath))
                        .build())
                .build();
        //我们直接获取模型响应结果，不使用assistantMessage进行包装
        String output = dashScopeChatModel.call(imageUserMessage);
        System.out.println(output);
    }

/** ------------------------SystemMessage ----------------***/

    /**
     * 测试System Message，引导模型的行为。你可以使用系统消息来设置语气、定义模型的角色并建立响应指南。
     *
     */
    @Test
    public void test3() {
        //测试定义模型的角色
        //创建chatModel
        ChatModel dashScopeChatModel = CreateChatClient.createDashScopeChatModel();
        //创建系统Message
        SystemMessage systemMessage = new SystemMessage("""
                你是一位资深的 Java 开发者，擅长 Web 框架。
                始终提供代码示例并解释你的推理。
                在解释中要简洁但透彻。
                请以HTML格式输出
                """);
        //创建UserMessage
        UserMessage userMessage = new UserMessage("如何创建REST API?");
        String output = dashScopeChatModel.call(systemMessage, userMessage);
        System.out.println(output);
    }


    /**
     * 测试System Message，引导模型的行为。你可以使用系统消息来设置语气、定义模型的角色并建立响应指南。
     *
     */
    @Test
    public void test2() {
        //测试设置语气
        //创建chatModel
        ChatModel dashScopeChatModel = CreateChatClient.createDashScopeChatModel();
        //创建系统message
        SystemMessage systemMessage = new SystemMessage("请使用东北人的语气回答问题。");
        UserMessage userMessage = new UserMessage("你是谁?");
        String output = dashScopeChatModel.call(systemMessage, userMessage);
        System.out.println(output);
    }


    /**
     * 基础使用
     */
    @Test
    public void test1() {
        //创建chatModel
        ChatModel dashScopeChatModel = CreateChatClient.createDashScopeChatModel();
        //创建系统message
        SystemMessage systemMessage = new SystemMessage("你是一个帮手。");
        //创建了userMessage
        UserMessage userMessage = new UserMessage("我想知道你的名字和你的能力");

        ChatResponse chatResponse = dashScopeChatModel.call(new Prompt(systemMessage, userMessage));
        //chatResponse 包含了assistantMessage
        System.out.println(chatResponse);
        //获取assistantMessage
        AssistantMessage assistantMessage = chatResponse.getResult().getOutput();
        System.out.println("assistantMessage" + assistantMessage);
    }
}
