package com.crm.query;

import lombok.Data;
import java.util.List;

/**
 * @name CustomerTrendQuery
 * @description 客户趋势查询参数对象
 */
@Data
public class CustomerTrendQuery {

    /**
     * 时间范围（例如：["2025-01-01", "2025-01-31"]）
     */
    private List<String> timeRange;

    /**
     * 交易类型（如：purchase、refund、recharge 等）
     */
    private String transactionType;

    /**
     * 时间格式化类型（如：day、month、year）
     */
    private String timeFormat;
}
