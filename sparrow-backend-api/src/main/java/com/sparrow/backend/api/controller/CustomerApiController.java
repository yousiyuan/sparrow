package com.sparrow.backend.api.controller;

import com.sparrow.backend.service.SparrowService;
import com.sparrow.common.JsonUtils;
import com.sparrow.framework.response.BaseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data/customer")
public class CustomerApiController {

    private SparrowService sparrowService;

    @Autowired
    public CustomerApiController(SparrowService sparrowService) {
        this.sparrowService = sparrowService;
    }

    @GetMapping("/list")
    public BaseResult customerList() {
        System.out.println(JsonUtils.to(new String[]{"z", "x", "b"}));
        return sparrowService.getCustomerList();
    }

    @GetMapping("/update/{id}")
    public String exceptionTest(@PathVariable Integer id) {
        if (id == 1) {
            throw new RuntimeException("测试服务降级、熔断");
        }
        return String.valueOf(id);
    }

}
