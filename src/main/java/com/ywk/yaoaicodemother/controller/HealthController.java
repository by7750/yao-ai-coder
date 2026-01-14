package com.ywk.yaoaicodemother.controller;

import com.ywk.yaoaicodemother.common.BaseResponse;
import com.ywk.yaoaicodemother.utils.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("/test")
public class HealthController {

    @GetMapping
    public BaseResponse<String> test() {
        return ResultUtils.success("ok");
    }
}
