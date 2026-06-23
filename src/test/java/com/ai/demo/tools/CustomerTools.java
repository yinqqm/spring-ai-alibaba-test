package com.ai.demo.tools;

import org.springframework.ai.tool.annotation.Tool;

public class CustomerTools {

    @Tool(description = "获取客户信息, 输入参数是用户id，类型是long，必须输入"
            ,resultConverter = CustomToolCallResultConverter.class
          //  ,returnDirect = true
    )
    public Customer getCustomerInfo(Long id){
        //模拟通过数据库查询
        Customer customer = new Customer();
        customer.setId(id);
        customer.setAge(28);
        customer.setUserName("五五");
        return customer;
    }
}
