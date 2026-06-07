package com.ai.demo.agent;

import org.springframework.ai.chat.model.ToolContext;

import java.util.function.BiFunction;

public class ChunkTool implements BiFunction<SearchToolInput, ToolContext, String> {
    @Override
    public String apply(SearchToolInput searchToolInput, ToolContext toolContext) {
        String query = searchToolInput.getQuery();
        return switch (query) {
            case "0" -> "chunk-0: Spring AI Alibaba Agent Framework";
            case "1" -> "chunk-1: ReactAgent 会在tool调用后继续回到 model";
            case "2" -> "chunk-2: 这能让一次 agent call 产生多次 model call";
            case "3" -> "chunk-3: 继续顺序读取下一段";
            case "4" -> "chunk-4: 还没有结束";
            case "5" -> "chunk-5: 还有更多内容";
            case "6" -> "chunk-6: 接近最后";
            case "7" -> "chunk-7: 最后一段";
            default -> "END";
        };
    }
}
