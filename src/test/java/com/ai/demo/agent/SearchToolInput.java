package com.ai.demo.agent;


import org.springframework.ai.tool.annotation.ToolParam;

public class SearchToolInput {
    @ToolParam(description = "指定的查询关键字",required = true)
    private String query;

    @ToolParam(description = "结果的匹配分数，可以为空; 为空则返回查询的所有结果", required = false)
    private Integer score;
    
    // getter 和 setter
    public String getQuery() { return query; }
    public void setQuery(String query) { this.query = query; }
}
