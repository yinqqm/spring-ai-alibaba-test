package com.ai.demo.agent;

import com.alibaba.cloud.ai.graph.OverAllState;
import com.alibaba.cloud.ai.graph.RunnableConfig;
import com.alibaba.cloud.ai.graph.agent.hook.AgentHook;
import com.alibaba.cloud.ai.graph.agent.hook.HookPosition;
import com.alibaba.cloud.ai.graph.agent.hook.HookPositions;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * AgentHook实例，在agent执行前和执行后执行
 */
@HookPositions({HookPosition.BEFORE_AGENT,HookPosition.AFTER_AGENT})
public class LoggingHook  extends AgentHook {
    /**
     * 调用agent前执行
     * @param state
     * @param config
     * @return
     */
    @Override
    public CompletableFuture<Map<String, Object>> beforeAgent(OverAllState state, RunnableConfig config) {
        System.out.println("Agent 开始执行");
        return super.beforeAgent(state, config);
    }

    @Override
    public CompletableFuture<Map<String, Object>> afterAgent(OverAllState state, RunnableConfig config) {
        System.out.println("Agent 执行完成");
        return super.afterAgent(state, config);
    }

    @Override
    public String getName() {
        return "logging";
    }

}
