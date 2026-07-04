package com.ai.demo.tools;

import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.ToolCallbackProvider;

import java.util.List;

public class CustomToolCallbackProvider implements ToolCallbackProvider {

    private final List<ToolCallback> toolCallbackList;
    //是否返回所有的toolCallback类
    private final Boolean returnAll;

    public CustomToolCallbackProvider(List<ToolCallback> toolCallbacks){
        this(toolCallbacks,Boolean.TRUE);
    }

    public CustomToolCallbackProvider(List<ToolCallback> toolCallbacks,Boolean returnAll){
        this.toolCallbackList = toolCallbacks;
        this.returnAll = returnAll;
    }

    @Override
    public ToolCallback[] getToolCallbacks() {
        //简单逻辑，如果配置了不返回所有的工具则只返回第一个
        if (returnAll){
            return toolCallbackList.toArray(new ToolCallback[0]);
        }else {
            return new ToolCallback[]{toolCallbackList.get(0)};
        }
    }
}
