package com.ai.demo.agent;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.function.BiFunction;

/**
 * 定义搜索工具
 *
 */
public class SearchTool implements BiFunction<SearchToolInput, ToolContext, String> {
    @Override
    public String apply(@ToolParam(description = "封装查询关键字的类") SearchToolInput query, ToolContext toolContext) {
        String callTool = (String) toolContext.getContext().getOrDefault("call_tool", "N");
        System.out.println("get data from mateData:"+callTool);

        //mock 搜索web
        return """
                    习近平就推动哲学社会科学高质量发展作出重要指示
                    习近平同美国总统特朗普在中南海小范围会晤
                    【微镜头·中美元首在北京举行会谈】“让2026年成为中美关系继往开来的历史性、标志性年份”
                    武汉票价最贵火车发车:20999元起
                    """;
    }
}
