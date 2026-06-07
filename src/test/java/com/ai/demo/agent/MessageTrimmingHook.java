package com.ai.demo.agent;

import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.hook.HookPosition;
import com.alibaba.cloud.ai.graph.agent.hook.HookPositions;
import com.alibaba.cloud.ai.graph.agent.hook.messages.AgentCommand;
import com.alibaba.cloud.ai.graph.agent.hook.messages.MessagesModelHook;
import com.alibaba.cloud.ai.graph.agent.hook.messages.UpdatePolicy;
import org.springframework.ai.chat.messages.Message;

import java.util.List;

/**
 * MessagesModelHook例子
 * 在模型执行前后拦截
 * 这个例子的作用是通过限制模型的消息的message数量，如果超过则进行截取s
 */
@HookPositions({HookPosition.BEFORE_MODEL,HookPosition.AFTER_MODEL})
public class MessageTrimmingHook extends MessagesModelHook {

    private static final Integer MAX_MESSAGES_NUM = 15;

    /**
     *
     * @param previousMessages 先前的message
     * @param config RunnableConfig配置信息
     * @return
     */
    @Override
    public AgentCommand beforeModel(List<Message> previousMessages, RunnableConfig config) {
        if(previousMessages.size()>MAX_MESSAGES_NUM){
            List<Message> trimmedMessages = previousMessages.subList(previousMessages.size() - MAX_MESSAGES_NUM, previousMessages.size());

            return new AgentCommand(trimmedMessages, UpdatePolicy.REPLACE);
        }
        return super.beforeModel(previousMessages, config);
    }

    @Override
    public AgentCommand afterModel(List<Message> previousMessages, RunnableConfig config) {
        return super.afterModel(previousMessages, config);
    }

    @Override
    public String getName() {
        return "message trimming hook";
    }
}
