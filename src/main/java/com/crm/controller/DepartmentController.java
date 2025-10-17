package com.crm.controller;

import com.crm.common.result.PageResult;
import com.crm.common.result.Result;
import com.crm.entity.Department;
import com.crm.query.DepartmentQuery;
import com.crm.query.IdQuery;
import com.crm.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.annotation.Resource;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/department")
@AllArgsConstructor
public class DepartmentController {

    private DepartmentService departmentService;

    @PostMapping("/page")
    @Operation(summary = "获取部门列表")
    public PageResult<Department> getPage(@RequestBody DepartmentQuery query) {
        return departmentService.getPage(query);
    }

    @PostMapping("list")
    @Operation(summary = "部门列表查询")
    public Result<List<Department>> getList() {
        return Result.ok(departmentService.getList());
    }

    @PostMapping("saveOrEdit")
    @Operation(summary = "保存或编辑部门")
    public Result saveOrEditDepartment(@RequestBody Department department) {
        departmentService.saveOrEdit(department);
        return Result.ok();
    }

    @PostMapping("remove")
    @Operation(summary = "删除部门")
    public Result removeDepartment(@RequestBody @Validated IdQuery query) {
        departmentService.removeDepartment(query);
        return Result.ok();
    }

}
