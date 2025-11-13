package com.crm.vo;

import lombok.Data;

/**
 * @name CustomerTrendVO
 * @description 客户交易趋势返回对象
 */
@Data
public class CustomerTrendVO {

    /**
     * 交易时间（格式如：2025-11-01、2025-11、2025 等）
     */
    private String tradeTime;

    /**
     * 交易数量（该时间点的交易笔数）
     */
    private Integer tradeCount;
}
