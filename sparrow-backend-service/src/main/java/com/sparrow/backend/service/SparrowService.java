package com.sparrow.backend.service;

import com.sparrow.backend.service.base.BaseService;
import com.sparrow.framework.enums.ReturnEnum;
import com.sparrow.framework.response.BaseResponse;
import lombok.extern.slf4j.Slf4j;
import com.sparrow.framework.response.BaseResult;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SparrowService extends BaseService {

    public SparrowService() {
    }

    public BaseResult getProductList() {
        return BaseResponse.success(ReturnEnum.SUCCESS, productBaseDao.selectAll());
    }

    public BaseResult getCustomerList() {
        return BaseResponse.success(ReturnEnum.SUCCESS, customerBaseDao.selectAll());
    }

}
