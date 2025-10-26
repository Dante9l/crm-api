package com.crm.controller;

import com.crm.common.result.Result;
import com.crm.service.CommonService;
import com.crm.vo.FileUrlVO;
import io.swagger.annotations.Api;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@Api(tags = "通用模块")
@RestController
@AllArgsConstructor
@RequestMapping("common")
@Tag(name = "通用接口", description = "通用接口")
public class CommonController {
    private final CommonService commonService;

    @PostMapping("upload")
    @Operation(summary = "文件上传")
    public Result<FileUrlVO> upload(@RequestParam MultipartFile multipartFile) {
        return Result.ok(commonService.upload(multipartFile));
    }
}
