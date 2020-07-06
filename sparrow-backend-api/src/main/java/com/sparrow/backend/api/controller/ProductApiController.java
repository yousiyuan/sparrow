package com.sparrow.backend.api.controller;

import com.sparrow.backend.service.SparrowService;
import com.sparrow.framework.response.BaseResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/data/product")
public class ProductApiController {

    private SparrowService sparrowService;

    @Autowired
    public ProductApiController(SparrowService sparrowService) {
        this.sparrowService = sparrowService;
    }

    @GetMapping("/list")
    public BaseResult productList() {
        return sparrowService.getProductList();
    }

    @GetMapping("/update/{id}")
    public String exceptionTest(@PathVariable Integer id) {
        if (id == 1) {
            throw new RuntimeException("测试服务降级、熔断");
        }
        return String.valueOf(id);
    }

}
