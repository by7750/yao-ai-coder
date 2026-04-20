package com.ywk.yaoaicodemother.controller;

import com.ywk.yaoaicodemother.common.BaseResponse;
import com.ywk.yaoaicodemother.utils.ResultUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/healthCheck")
    public BaseResponse<String> healthCheck() {
        return ResultUtils.success("ok");
    }
}
