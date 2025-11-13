package com.crm.service;

import com.crm.common.result.PageResult;
import com.crm.entity.OperLog;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.query.CustomerTrendQuery;
import com.crm.query.OperLogQuery;

import java.util.List;
import java.util.Map;

/**
 * <p>
 * 操作日志记录 服务类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface OperLogService extends IService<OperLog> {

    void recordOperLog(OperLog operLog);

    PageResult<OperLog> page(OperLogQuery operLogQuery);

    Map<String, List> getCustomerTrendData(CustomerTrendQuery query);
}
