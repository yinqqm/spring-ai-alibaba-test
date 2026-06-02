package com.ai.demo.agent;

import org.springframework.ai.chat.model.ToolContext;

import java.util.function.BiFunction;

public class WeatherSearchTool implements BiFunction<SearchToolInput, ToolContext, String> {
    @Override
    public String apply(SearchToolInput searchToolInput, ToolContext toolContext) {
        //mock 搜索web
        return searchToolInput.getQuery()+"的天气是大晴天，温度：20度至35度，体感闷热，紫外线强，记得防晒。祝你生活愉快！！";
    }
}
