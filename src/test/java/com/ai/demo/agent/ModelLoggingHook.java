package com.ai.demo.agent;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.hook.HookPosition;
import com.alibaba.cloud.ai.graph.agent.hook.HookPositions;
import com.alibaba.cloud.ai.graph.agent.hook.messages.AgentCommand;
import com.alibaba.cloud.ai.graph.agent.hook.messages.MessagesModelHook;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

/**
 * 记录model call的日志
 */
@HookPositions({HookPosition.BEFORE_MODEL,HookPosition.AFTER_MODEL})
public class ModelLoggingHook extends MessagesModelHook {

    @Override
    public AgentCommand beforeModel(List<Message> previousMessages, RunnableConfig config) {
        System.out.println("----调用模型前---");
        return super.beforeModel(previousMessages, config);
    }

    @Override
    public AgentCommand afterModel(List<Message> previousMessages, RunnableConfig config) {
        System.out.println("----调用模型后---");
        return super.afterModel(previousMessages, config);
    }

    @Override
    public String getName() {
        return "ModelLoggingHook";
    }
}
