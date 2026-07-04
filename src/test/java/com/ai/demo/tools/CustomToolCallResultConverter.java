package com.ai.demo.tools;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.Nullable;
import org.springframework.ai.tool.execution.ToolCallResultConverter;

import java.lang.reflect.Type;

public class CustomToolCallResultConverter implements ToolCallResultConverter {
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * ToolCallResultConverter 接口的作用是将工具方法的返回值对象转换为字符串，
     * 因为 AI 模型最终接收的是文本格式的结果。你需要将 Customer 对象序列化为 JSON 或其他可读格式。
     * @param result
     * @param returnType
     * @return
     */
    @Override
    public String convert(@Nullable Object result, @Nullable Type returnType) {
        if (result == null) {
            return "No customer found";
        }

        // 如果结果是 Customer 类型，转换为格式化的字符串
        if (result instanceof Customer customer) {
            try {
                // 方式1: 转换为 JSON 格式（推荐）
                String json = objectMapper.writeValueAsString(customer);
//                return json;

                 //方式2: 转换为人类可读的自然语言描述
                 return String.format("Customer found - ID: %d, Name: %s, Age: %d",
                         customer.getId(),
                         customer.getUserName(),
                         customer.getAge());

            } catch (JsonProcessingException e) {
                return "Error converting customer data: " + e.getMessage();
            }
        }

        // 其他类型的默认处理
        return result.toString();
    }
}
