package com.crm.controller;

import com.crm.common.result.PageResult;
import com.crm.common.result.Result;
import com.crm.entity.FollowUp;
import com.crm.entity.Lead;
import com.crm.query.IdQuery;
import com.crm.query.LeadQuery;
import com.crm.service.LeadService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@RestController
@RequestMapping("/lead")
@AllArgsConstructor
public class LeadController {
    LeadService leadService;

    @PostMapping("/page")
    @Operation(summary = "分页列表")
    public Result<PageResult<Lead>> getPage(@RequestBody @Validated LeadQuery  query){
        return Result.ok(leadService.getPage(query));
    }

    @PostMapping("/saveOrEdit")
    @Operation(summary = "保存或编辑")
    public Result saveOrEdit(@RequestBody Lead lead){
        leadService.saveOrEdit(lead);
        return Result.ok();
    }

    @PostMapping("/followLead")
    @Operation(summary = "跟进")
    public Result followLead(@RequestBody FollowUp  lead){
        leadService.followUp(lead);
        return Result.ok();
    }

    @PostMapping("/toCustomer")
    @Operation(summary = "转客户")
    public Result convertToCustomer(@RequestBody @Validated IdQuery idQuery){
        leadService.convertToCustomer(idQuery);
        return Result.ok();
    }

}
