package com.ai.demo;

import com.ai.demo.tool.CreateChatClient;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.StateGraph;
import com.alibaba.cloud.ai.graph.agent.ReactAgent;
import com.alibaba.cloud.ai.graph.checkpoint.savers.MemorySaver;
import com.alibaba.cloud.ai.graph.checkpoint.savers.mysql.CreateOption;
import com.alibaba.cloud.ai.graph.checkpoint.savers.mysql.MysqlSaver;
import com.alibaba.cloud.ai.graph.exception.GraphRunnerException;
import com.mysql.cj.jdbc.MysqlDataSource;
import org.junit.Test;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.model.ChatModel;

public class MemoryTester {


    /**
     * Mysql测试2
     * @throws GraphRunnerException
     */
    @Test
    public void test3() throws GraphRunnerException {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost:3306/ali_ai");
        dataSource.setUser("root");
        dataSource.setPassword("root");
        MysqlSaver mysqlSaver = MysqlSaver.builder()
                .createOption(CreateOption.CREATE_IF_NOT_EXISTS)
                .stateSerializer(StateGraph.DEFAULT_JACKSON_SERIALIZER)
                .dataSource(dataSource)
                .build();

        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        ReactAgent mysqlAgent = ReactAgent.builder()
                .saver(mysqlSaver)
                .model(chatModel)
                .name("Mysql_Agent")
                .build();
        //通过RunnableConfig可以控制不同的会话，用它来管理不同的user的会话信息。
        RunnableConfig config = RunnableConfig.builder()
                .threadId("user0001")
                .build();
        AssistantMessage message = mysqlAgent.call("我是用户0001", config);
        System.out.println(message);


    }


    /**
     * MySQLSaver测试
     */
    @Test
    public void test2() throws GraphRunnerException {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setURL("jdbc:mysql://localhost:3306/ali_ai");
        dataSource.setUser("root");
        dataSource.setPassword("root");
        MysqlSaver mysqlSaver = MysqlSaver.builder()
                .createOption(CreateOption.CREATE_IF_NOT_EXISTS)
                .stateSerializer(StateGraph.DEFAULT_JACKSON_SERIALIZER)
                .dataSource(dataSource)
                .build();

        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        ReactAgent mysqlAgent = ReactAgent.builder()
                .saver(mysqlSaver)
                .model(chatModel)
                .name("Mysql_Agent")
                .build();
        //不指定的话有一个默认的session
        AssistantMessage message = mysqlAgent.call("我是谁?");
        System.out.println(message);


    }

    /**
     * 使用MemorySaver
     */
    @Test
    public void test1() throws GraphRunnerException {
        ChatModel chatModel = CreateChatClient.createDashScopeChatModel();
        ReactAgent agent = ReactAgent.builder()
                .name("my_agent")
                .model(chatModel)
                .saver(new MemorySaver())
                .build();
        agent.call("你好，我是Bob");
        AssistantMessage message = agent.call("我是谁?");
        System.out.println(message);

    }

}
