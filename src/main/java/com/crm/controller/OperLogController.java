package com.crm.controller;

import com.crm.common.result.PageResult;
import com.crm.common.result.Result;
import com.crm.entity.OperLog;
import com.crm.query.OperLogQuery;
import com.crm.service.OperLogService;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 * 操作日志记录 前端控制器
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */

@Api(tags = "操作日志")
@RestController
@RequestMapping("/operLog")
@AllArgsConstructor
public class OperLogController {

    private final OperLogService operLogService;

    @RequestMapping("/page")
    public Result<PageResult<OperLog>> page(OperLogQuery operLogQuery) {
        return Result.ok(operLogService.page(operLogQuery));
    }
}
