package com.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.exception.ServerException;
import com.crm.common.result.PageResult;
import com.crm.convert.CustomerConverter;
import com.crm.entity.Customer;
import com.crm.entity.SysManager;
import com.crm.mapper.CustomerMapper;
import com.crm.query.CustomerQuery;
import com.crm.query.IdQuery;
import com.crm.security.user.SecurityUser;
import com.crm.service.CustomerService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.crm.utils.ExcelUtils;
import com.crm.vo.CustomerVO;
import com.github.yulichang.wrapper.MPJLambdaWrapper;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Service
public class CustomerServiceImpl extends ServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Override
    public PageResult<CustomerVO> getPage(CustomerQuery query) {

        // 1. 声明分页参数
        Page<CustomerVO> page = new Page<>(query.getPage(), query.getLimit());

        // 2. 构建查询关系（联表）
        MPJLambdaWrapper<Customer> wrapper = new MPJLambdaWrapper<>();
        wrapper.selectAll(Customer.class)
                // 关联创建人、所属员工的账号字段
                .selectAs("o", SysManager::getAccount, CustomerVO::getOwnerName)
                .selectAs("c", SysManager::getAccount, CustomerVO::getCreaterName)
                .leftJoin(SysManager.class, "o", SysManager::getId, Customer::getOwnerId)
                .leftJoin(SysManager.class, "c", SysManager::getId, Customer::getCreaterId);

        // 3. 构建查询条件
        if (StringUtils.isNotBlank(query.getName())) {
            wrapper.like(Customer::getName, query.getName());
        }
        if (StringUtils.isNotBlank(query.getPhone())) {
            wrapper.like(Customer::getPhone, query.getPhone());
        }
        if (query.getLevel() != null) {
            wrapper.eq(Customer::getLevel, query.getLevel());
        }
        if (query.getSource() != null) {
            wrapper.eq(Customer::getSource, query.getSource());
        }
        if (query.getFollowStatus() != null) {
            wrapper.eq(Customer::getFollowStatus, query.getFollowStatus());
        }
        if (query.getIsPublic() != null) {
            wrapper.eq(Customer::getIsPublic, query.getIsPublic());
        }

        // 4. 根据创建时间倒序排序
        wrapper.orderByDesc(Customer::getCreateTime);

        // 5. 执行分页查询
        Page<CustomerVO> result = baseMapper.selectJoinPage(page, CustomerVO.class, wrapper);

        // 6. 封装结果返回
        return new PageResult<>(result.getRecords(), page.getTotal());
    }

    @Override
    public void saveOrUpdate(CustomerVO customerVO) {
        // 1. 根据手机号构建查询条件
        LambdaQueryWrapper<Customer> wrapper = new LambdaQueryWrapper<Customer>()
                .eq(Customer::getPhone, customerVO.getPhone());

        if (customerVO.getId() == null) {
            // ========== 新增客户 ==========

            // 1. 判断手机号是否已存在
            Customer customer = baseMapper.selectOne(wrapper);
            if (customer != null) {
                throw new ServerException("该手机号客户已经存在，请勿重复添加客户信息");
            }

            // 2. 转换 VO → 实体
            Customer convertCustomer = CustomerConverter.INSTANCE.convert(customerVO);

            // 3. 设置创建人、负责人为当前登录管理员
            Integer managerId = SecurityUser.getManagerId();
            convertCustomer.setCreaterId(managerId);
            convertCustomer.setOwnerId(managerId);
            convertCustomer.setDeleteFlag((byte) 0);
            convertCustomer.setFollowStatus((byte) 0);

            // 4. 执行插入
            baseMapper.insert(convertCustomer);

        } else {
            // ========== 编辑客户 ==========

            // 1. 判断手机号是否与其他客户重复
            wrapper.ne(Customer::getId, customerVO.getId());
            Customer customer = baseMapper.selectOne(wrapper);
            if (customer != null) {
                throw new ServerException("该手机号客户已经存在，请勿重复添加客户信息");
            }

            // 2. 转换 VO → 实体
            Customer convertCustomer = CustomerConverter.INSTANCE.convert(customerVO);

            // 3. 执行更新
            baseMapper.updateById(convertCustomer);
        }
    }

    @Override
    public void removeCustomer(List<Integer> ids) {
        removeByIds(ids);
    }

    /**
     * 导出客户信息
     * 响应类型为二进制流，前端调用时需设置 responseType = 'blob'
     */
    @Override
    public void exportCustomer(CustomerQuery query, HttpServletResponse response) {
        // 1. 构建查询条件
        MPJLambdaWrapper<Customer> wrapper = selectCondition(query);

        // 2. 查询数据
        List<CustomerVO> list = baseMapper.selectJoinList(CustomerVO.class, wrapper);

        // 3. 写入 Excel 并输出
        ExcelUtils.writeExcel(response, list, "客户信息", "客户信息", CustomerVO.class);
    }


    /**
     * 构建客户查询条件
     */
    private MPJLambdaWrapper<Customer> selectCondition(CustomerQuery query) {
        MPJLambdaWrapper<Customer> wrapper = new MPJLambdaWrapper<>();

        // 1. 构建查询关联关系
        wrapper.selectAll(Customer.class)
                .selectAs("o", SysManager::getAccount, CustomerVO::getOwnerName)
                .selectAs("c", SysManager::getAccount, CustomerVO::getCreaterName)
                .leftJoin(SysManager.class, "o", SysManager::getId, Customer::getOwnerId)
                .leftJoin(SysManager.class, "c", SysManager::getId, Customer::getCreaterId);

        // 2. 构建查询条件
        if (StringUtils.isNotBlank(query.getName())) {
            wrapper.like(Customer::getName, query.getName());
        }
        if (StringUtils.isNotBlank(query.getPhone())) {
            wrapper.like(Customer::getPhone, query.getPhone());
        }
        if (query.getLevel() != null) {
            wrapper.eq(Customer::getLevel, query.getLevel());
        }
        if (query.getSource() != null) {
            wrapper.eq(Customer::getSource, query.getSource());
        }
        if (query.getFollowStatus() != null) {
            wrapper.eq(Customer::getFollowStatus, query.getFollowStatus());
        }
        if (query.getIsPublic() != null) {
            wrapper.eq(Customer::getIsPublic, query.getIsPublic());
        }

        // 3. 排序
        wrapper.orderByDesc(Customer::getCreateTime);

        return wrapper;
    }

    @Override
    public void customerToPublicPool(IdQuery query) {
        Customer customer = baseMapper.selectById(query.getId());
        if (customer == null) {
            throw new ServerException("客户不存在");
        }
        customer.setIsPublic((byte) 1);
        customer.setOwnerId(null);
        baseMapper.updateById(customer);
    }

    @Override
    public void publicPoolToPrivate(IdQuery query) {
        Customer customer = baseMapper.selectById(query.getId());
        if (customer == null) {
            throw new ServerException("客户不存在");
        }
        customer.setIsPublic((byte) 0);
        customer.setOwnerId(SecurityUser.getManagerId());
        baseMapper.updateById(customer);
    }
}
