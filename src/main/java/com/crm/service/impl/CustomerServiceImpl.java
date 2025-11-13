package com.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.exception.ServerException;
import com.crm.common.result.PageResult;
import com.crm.convert.CustomerConvert;
import com.crm.entity.Customer;
import com.crm.entity.SysManager;
import com.crm.mapper.CustomerMapper;
import com.crm.query.CustomerQuery;
import com.crm.query.CustomerTrendQuery;
import com.crm.query.IdQuery;
import com.crm.security.user.SecurityUser;
import com.crm.service.CustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.utils.DateUtils;
import com.crm.utils.ExcelUtils;
import com.crm.vo.CustomerTrendVO;
import com.crm.vo.CustomerVO;
import com.fhs.common.utils.StringUtil;
import com.github.yulichang.base.MPJBaseMapper;
import com.github.yulichang.query.MPJLambdaQueryWrapper;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.Security;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.crm.utils.DateUtils.*;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Service
@Slf4j
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Override
    public PageResult<CustomerVO> getPage(CustomerQuery query) {
        Page<CustomerVO> page = new Page<>(query.getPage(), query.getLimit());
        MPJLambdaWrapper< Customer> wrapper = selection(query);
        Page<CustomerVO> result = baseMapper.selectJoinPage(page, CustomerVO.class, wrapper);
        return new PageResult<>(result.getRecords(),result.getTotal());
    }

    @Override
    public void exportCustomer(CustomerQuery query, HttpServletResponse response) {
        MPJLambdaWrapper<Customer> wrap = selection(query);
        List<CustomerVO> list = baseMapper.selectJoinList(CustomerVO.class, wrap);
        ExcelUtils.writeExcel(response,list,"客户列表","客户列表",CustomerVO.class);
    }

    @Override
    public void saveOrUpdate(CustomerVO customerVO) {
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<Customer>().eq(Customer::getPhone,customerVO.getPhone());
        if (customerVO.getId() == null){
            //1.判断手机号信息是否存在
            Customer customer = baseMapper.selectOne(wrapper);
            if (customer != null){
                throw new ServerException("该手机号客户已经存在,请勿重复添加客户信息");
            }

            Customer convertCustomer = CustomerConvert.INSTANCE.convert(customerVO);
            //2.获取新增的管理员信息
            Integer managerId = SecurityUser.getManagerId();
            convertCustomer.setOwnerId(managerId);
            convertCustomer.setCreaterId(managerId);
            baseMapper.insert(convertCustomer);
        }else {
            wrapper.ne(Customer::getId,customerVO.getId());
            Customer customer = baseMapper.selectOne(wrapper);
            if (customer != null){
                throw new ServerException("该手机号客户已经存在,请勿重复添加客户信息");
            }
            Customer convertCustomer = CustomerConvert.INSTANCE.convert(customerVO);
            baseMapper.updateById(convertCustomer);
        }
    }

    @Override
    public void customerToPublicPool(IdQuery idQuery) {
        Customer customer = baseMapper.selectById(idQuery.getId());
        log.info("客户信息:{}",customer);
        if (customer == null){
            throw new ServerException("客户不存在,无法转入公海");
        }
        customer.setIsPublic(1);
        customer.setOwnerId(null);
        baseMapper.updateById(customer);
    }

    @Override
    public void removeCustomer(List<Integer> ids) {
        removeByIds(ids);
    }

    @Override
    public void publicPoolToPrivate(IdQuery idQuery) {
        Customer customer = baseMapper.selectById(idQuery.getId());
        log.info("客户信息:{}",customer);
        if (customer == null){
            throw new ServerException("客户不存在,无法转回个人");
        }
        customer.setIsPublic(0);
        Integer ownerId = SecurityUser.getManagerId();
        customer.setOwnerId(ownerId);
        baseMapper.updateById(customer);
    }

    private  MPJLambdaWrapper<Customer> selection(CustomerQuery query){
        MPJLambdaWrapper<Customer> wrapper = new MPJLambdaWrapper<>();

        wrapper.selectAll(Customer.class)
                .selectAs("o", SysManager::getAccount,CustomerVO::getOwnerName)
                .selectAs("c",SysManager::getAccount,CustomerVO::getCreatorName)
                .leftJoin(SysManager.class,"o", SysManager::getId, Customer::getOwnerId)
                .leftJoin(SysManager.class,"c", SysManager::getId, Customer::getCreaterId);

        if (StringUtils.isNotBlank(query.getName())){
            wrapper.like(Customer::getName,query.getName());
        }
        if (StringUtils.isNotBlank(query.getPhone())){
            wrapper.like(Customer::getPhone,query.getPhone());
        }
        if (query.getLevel() != null){
            wrapper.eq(Customer::getLevel,query.getLevel());
        }
        if (query.getSource() != null){
            wrapper.eq(Customer::getSource,query.getSource());
        }
        if (query.getFollowStatus() != null){
            wrapper.eq(Customer::getFollowStatus,query.getFollowStatus());
        }
        if (query.getIsPublic() != null){
            wrapper.eq(Customer::getIsPublic,query.getIsPublic());
        }
        wrapper.orderByDesc(Customer::getCreateTime);

        return wrapper;
    }

    @Override
    public Map<String, List> getCustomerTrendData(CustomerTrendQuery query) {
        //       处理不同请求类型的时间
        //        x轴时间数据
        List<String> timeList = new ArrayList<>();
        //        统计客户变化数据
        List<Integer> countList = new ArrayList<>();
        List<CustomerTrendVO> tradeStatistics;

        if ("day".equals(query.getTransactionType())) {
            LocalDateTime now = LocalDateTime.now();
            // 截断毫秒和纳秒部分影响sql 查询结果
            LocalDateTime truncatedNow = now.truncatedTo(ChronoUnit.SECONDS);
            LocalDateTime startTime = now.withHour(0).withMinute(0).withSecond(0).truncatedTo(ChronoUnit.SECONDS);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            List<String> timeRange = new ArrayList<>();
            timeRange.add(formatter.format(startTime));
            timeRange.add(formatter.format(truncatedNow));
            query.setTimeRange(timeRange);
            timeList = getHourData(timeList);
            tradeStatistics = baseMapper.getTradeStatistics(query);
        } else if ("monthrange".equals(query.getTransactionType())) {
            query.setTimeFormat("'%Y-%m'");
            timeList = getMonthInRange(query.getTimeRange().get(0), query.getTimeRange().get(1));
            tradeStatistics = baseMapper.getTradeStatisticsByDay(query);
        } else if ("week".equals(query.getTransactionType())) {
            timeList = getWeekInRange(query.getTimeRange().get(0), query.getTimeRange().get(1));
            tradeStatistics = baseMapper.getTradeStatisticsByWeek(query);
        } else {
            query.setTimeFormat("'%Y-%m-%d'");
            timeList = DateUtils.getDatesInRange(query.getTimeRange().get(0), query.getTimeRange().get(1));
            tradeStatistics = baseMapper.getTradeStatisticsByDay(query);
        }

        //        匹配时间点查询到的数据，没有值的默认为0
        List<CustomerTrendVO> finalTradeStatistics = tradeStatistics;

        timeList.forEach(item -> {
            // 'item' 可能是 "HH", "YYYY-MM", "WW", "YYYY-MM-DD"

            int totalCountForPeriod = finalTradeStatistics.stream()
                    .filter(vo -> {
                        String tradeTime = vo.getTradeTime();
                        if (tradeTime == null) {
                            return false; // 过滤掉空数据
                        }

                        String type = query.getTransactionType();

                        // 【重构】 Filter 内部必须镜像外部的查询逻辑

                        if ("day".equals(type)) {
                            // 'item' 是 "HH", 'tradeTime' 也是 "HH"
                            return tradeTime.length() >= 2 && item.substring(0, 2).equals(tradeTime.substring(0, 2));

                        } else if ("week".equals(type)) {
                            // 【已修复】
                            // 'item' 是 "44", 'tradeTime' 是 "2025-W44"
                            // 检查 "2025-W44" 的最后两位是否等于 "44"
                            // (假设格式为 YYYY-W##，长度为8)
                            return tradeTime.length() >= 7 && tradeTime.substring(6).equals(item);
                            // 或者使用更安全的分隔符逻辑:
                            // String[] parts = tradeTime.split("-W");
                            // return parts.length == 2 && parts[1].equals(item);

                        } else if ("monthrange".equals(type)) {
                            // 'item' 是 "2025-10", 'tradeTime' 是 "2025-10-03"
                            return tradeTime.length() >= 7 && tradeTime.substring(0, 7).equals(item);

                        } else {
                            // 默认 (daterange)
                            // 'item' 是 "2025-10-03", 'tradeTime' 也是 "2025-10-03"
                            return tradeTime.equals(item);
                        }
                    })
                    .mapToInt(CustomerTrendVO::getTradeCount) // 提取所有匹配项的 count
                    .sum(); // 计算它们的总和

            countList.add(totalCountForPeriod);
        });

        Map<String, List> result = new HashMap<>();
        result.put("timeList", timeList);
        result.put("countList", countList);
        return result;
    }

}
