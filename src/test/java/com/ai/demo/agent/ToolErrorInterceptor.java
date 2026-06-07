package com.ai.demo.agent;

import com.alibaba.cloud.ai.graph.agent.interceptor.ToolCallHandler;
import com.alibaba.cloud.ai.graph.agent.interceptor.ToolCallRequest;
import com.alibaba.cloud.ai.graph.agent.interceptor.ToolCallResponse;
import com.alibaba.cloud.ai.graph.agent.interceptor.ToolInterceptor;

public class ToolErrorInterceptor extends ToolInterceptor {
    @Override
    public ToolCallResponse interceptToolCall(ToolCallRequest request, ToolCallHandler handler) {
        try {
            //对查询的词语进行审计，不能是敏感词
            String arguments = request.getArguments();
            if(arguments.contains("昨天")){
                ToolCallResponse toolCallResponse = ToolCallResponse.of(request.getToolCallId(), request.getToolName(),
                        "查询条件中包含敏感词语，请重新输入查询条件。模型不能再次修改参数请求二次调用。");
                return toolCallResponse;
            }
            //调用工具
            ToolCallResponse callResponse = handler.call(request);
            return callResponse;
        } catch (Exception e) {
            return ToolCallResponse.of(request.getToolCallId(), request.getToolName(),
                    "Tool failed: " + e.getMessage());
        }
    }

    @Override
    public String getName() {
        return "ToolErrorInterceptor";
    }
}
