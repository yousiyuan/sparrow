package com.sparrow.backend.dao.mapper;

import com.sparrow.backend.pojo.Product;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;

import java.util.List;

public interface ProductMapper extends Mapper<Product>, MySqlMapper<Product>, IdsMapper<Product> {

    List<Product> selectProductByCategory(Integer categoryId);

}
