package com.crm.service;

import com.crm.common.result.PageResult;
import com.crm.entity.Product;
import com.baomidou.mybatisplus.extension.service.IService;
import com.crm.query.ProductQuery;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author crm
 * @since 2025-10-12
 */
public interface ProductService extends IService<Product> {

    PageResult<Product> getPage(ProductQuery query);

    /**
     * 保存或编辑
     * @param product
     */
    void saveOrEdit(Product product);

    /**
     * 批量更新状态
     */
    void batchUpdateStatus();

    void changeStatus(Product product);

}
