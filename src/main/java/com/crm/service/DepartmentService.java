package com.crm.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.result.PageResult;
import com.crm.entity.Department;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.query.DepartmentQuery;
import com.crm.query.IdQuery;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface DepartmentService extends IService<Department> {

    /**
     * 获取部门列表
     * @param query 查询参数
     * @return 部门列表
     */
    PageResult<Department> getPage(DepartmentQuery query);

    List<Department>getList();

    void saveOrEdit(Department department);

    void removeDepartment(IdQuery query);
}
