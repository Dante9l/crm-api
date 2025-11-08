package com.crm.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.crm.common.exception.ServerException;
import com.crm.common.result.PageResult;
import com.crm.convert.CustomerConvert;
import com.crm.entity.Customer;
import com.crm.entity.FollowUp;
import com.crm.entity.Lead;
import com.crm.mapper.CustomerMapper;
import com.crm.mapper.FollowUpMapper;
import com.crm.mapper.LeadMapper;
import com.crm.query.IdQuery;
import com.crm.query.LeadQuery;
import com.crm.security.user.SecurityUser;
import com.crm.service.LeadService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
@Service
@AllArgsConstructor
public class LeadServiceImpl extends ServiceImpl<LeadMapper, Lead> implements LeadService {

    private CustomerMapper customerMapper;
    private FollowUpMapper followUpMapper;
    @Override
    public PageResult<Lead> getPage(LeadQuery query) {
        Page<Lead> page = new Page<>(query.getPage(), query.getLimit());
        LambdaQueryWrapper<Lead> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.isNotBlank(query.getName())){
            wrapper.like(Lead::getName, query.getName());
        }
        if (query.getFollowStatus() != null){
            wrapper.eq(Lead::getFollowStatus, query.getFollowStatus());
        }
        if (query.getStatus() != null){
            wrapper.eq(Lead::getStatus, query.getStatus());
        }
        wrapper.orderByDesc(Lead::getCreateTime);
        Page<Lead> leadPage = baseMapper.selectPage(page, wrapper);

        return new PageResult<>(leadPage.getRecords(),leadPage.getTotal());
    }

    @Override
    public void saveOrEdit(Lead lead) {
        LambdaQueryWrapper<Lead> wrapper = new LambdaQueryWrapper<Lead>().eq(Lead::getName,lead.getName());
        Customer customer = customerMapper.selectOne(new LambdaQueryWrapper<Customer>().eq(Customer::getPhone, lead.getPhone()));
        if (customer != null && lead.getStatus() == 1){
            throw new ServerException("客户已存在");
        }
        if (lead.getId() == null){
            //1.判断手机号信息是否存在
            Lead selected = baseMapper.selectOne(wrapper);
            if (selected != null){
                throw new ServerException("线索已存在");
            }
            //2.保存
            baseMapper.insert(lead);
        }else{
            wrapper.ne(Lead::getId, lead.getId());
            Lead selectLead = baseMapper.selectOne(wrapper);
            if (selectLead != null) {
                throw new ServerException("该线索名称已经存在");
            }
            //3.修改
            baseMapper.updateById(lead);
        }
    }

    @Override
    public void followUp(FollowUp followUp) {
        Lead lead = baseMapper.selectById(followUp.getId());
        if (lead == null){
            throw new ServerException("该线索不存在");
        }
        lead.setFollowStatus(followUp.getFollowType());
        baseMapper.updateById(lead);
        followUp.setId(null);
        followUp.setCustomerId(lead.getId());
        followUp.setTargetType(1);
        followUpMapper.insert(followUp);
    }

    @Override
    public void convertToCustomer(IdQuery idQuery) {
        Lead lead = baseMapper.selectById(idQuery.getId());
        if (lead == null){
            throw new ServerException("该线索不存在");
        }
        Customer customer = CustomerConvert.INSTANCE.leadConvert(lead);
        customer.setId(null);
        customer.setCreaterId(SecurityUser.getManagerId());
        customerMapper.insert(customer);
        lead.setStatus(1);
        baseMapper.updateById(lead);
    }
}
