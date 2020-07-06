package com.sparrow.backend.service.base;

import com.sparrow.backend.dao.ProductDao;
import com.sparrow.backend.dao.base.BaseDao;
import com.sparrow.backend.pojo.Customer;
import com.sparrow.backend.pojo.Product;
import org.springframework.beans.factory.annotation.Autowired;

public abstract class BaseService {

    @Autowired
    protected ProductDao productDao;

    @Autowired
    protected BaseDao<Product> productBaseDao;

    @Autowired
    protected BaseDao<Customer> customerBaseDao;

}
