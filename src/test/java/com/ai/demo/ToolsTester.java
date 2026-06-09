package com.ai.demo;

import com.ai.demo.tool.CreateChatClient;
import com.ai.demo.tools.DateTimeTools;
import org.junit.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.model.ChatModel;

public class ToolsTester {


    /**
     * 工具调用，模拟设置闹钟
     * 通过设置多个tools来实现
     */
    @Test
    public void test2(){
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
