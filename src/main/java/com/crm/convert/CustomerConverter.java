package com.crm.convert;

import com.crm.entity.Customer;
import com.crm.vo.CustomerVO;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface CustomerConverter {
    CustomerConverter INSTANCE = Mappers.getMapper(CustomerConverter.class);

    Customer convert(CustomerVO vo);
}
