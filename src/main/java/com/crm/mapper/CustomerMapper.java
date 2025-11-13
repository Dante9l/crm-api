package com.crm.mapper;

import com.crm.entity.Customer;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.crm.query.CustomerTrendQuery;
import com.crm.vo.CustomerTrendVO;
import com.github.yulichang.base.MPJBaseMapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface CustomerMapper extends MPJBaseMapper<Customer> {

    /**
     * 通用交易趋势统计（可根据时间类型动态选择按天/周/月聚合）
     *
     * @param query 查询条件
     * @return 客户交易趋势列表
     */
    List<CustomerTrendVO> getTradeStatistics(@Param("query") CustomerTrendQuery query);

    /**
     * 按天统计交易趋势
     *
     * @param query 查询条件
     * @return 每日交易统计数据
     */
    List<CustomerTrendVO> getTradeStatisticsByDay(@Param("query") CustomerTrendQuery query);

    /**
     * 按周统计交易趋势
     *
     * @param query 查询条件
     * @return 每周交易统计数据
     */
    List<CustomerTrendVO> getTradeStatisticsByWeek(@Param("query") CustomerTrendQuery query);

}
