package com.sparrow.backend.dao.base;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.apache.ibatis.session.RowBounds;
import org.springframework.core.ResolvableType;
import org.springframework.util.Assert;
import tk.mybatis.mapper.common.IdsMapper;
import tk.mybatis.mapper.common.Mapper;
import tk.mybatis.mapper.common.MySqlMapper;
import tk.mybatis.mapper.entity.Example;

import java.io.Serializable;
import java.util.List;

public abstract class BaseDao<T extends Serializable> {

    private String genericTypeName;

    public BaseDao(Mapper<T> mapper, MySqlMapper<T> mySqlMapper, IdsMapper<T> idsMapper) {
        this.mapper = mapper;
        this.mySqlMapper = mySqlMapper;
        this.idsMapper = idsMapper;
        Class<?> genericType = ResolvableType.forInstance(this).getSuperType().getGeneric(0).resolve();
        Assert.notNull(genericType, "当前类的泛型不存在");
        genericTypeName = genericType.getTypeName();

        //Method selectForPageList = ReflectionUtils.findMethod(this.getClass(), "selectForPageList", Integer.class, Integer.class, String.class, Object.class);
        //ResolvableType resolvableType = ResolvableType.forMethodReturnType(selectForPageList);
    }

    /**
     * Dao层的mapper接口必须能找到继承Mapper《T》的接口，此处才能注入成功
     */
    private Mapper<T> mapper;

    /**
     * Dao层的mapper接口能找到继承MySqlMapper《T》的接口，此处才能注入成功
     */
    private MySqlMapper<T> mySqlMapper;

    /**
     * Dao层的mapper接口能找到继承MySqlMapper《T》的接口，此处才能注入成功
     */
    private IdsMapper<T> idsMapper;

    public String getGenericTypeName() {
        return this.genericTypeName;
    }

    //region Mapper
    // 保存一个实体，null属性也会保存
    public int insert(T record) {
        return mapper.insert(record);
    }

    // 保存一个实体，null属性不会保存
    public int insertSelective(T record) {
        return mapper.insertSelective(record);
    }

    // 根据实体属性作为条件进行删除，查询条件使用等号
    public int delete(T record) {
        return mapper.delete(record);
    }

    // 根据主键进行删除，查询条件使用等号
    public int deleteByPrimaryKey(Object primaryKey) {
        return mapper.deleteByPrimaryKey(primaryKey);
    }

    // 根据主键更新属性，null的值也会更新
    public int updateByPrimaryKey(T record) {
        return mapper.updateByPrimaryKey(record);
    }

    // 根据主键更新属性不为null的值
    public int updateByPrimaryKeySelective(T record) {
        return mapper.updateByPrimaryKeySelective(record);
    }

    // 根据实体中的属性值进行查询，查询条件使用等号
    public List<T> select(T record) {
        return mapper.select(record);
    }

    // 查询全部结果，select(null)方法能达到同样的效果
    public List<T> selectAll() {
        return mapper.selectAll();
    }

    // 根据实体中的属性查询总数，查询条件使用等号
    public int selectCount(T record) {
        return mapper.selectCount(record);
    }

    // 根据实体中的属性进行查询，只能有一个返回值，有多个结果是抛出异常，查询条件使用等号
    public T selectOne(T record) {
        return mapper.selectOne(record);
    }

    // 根据主键进行查询，查询条件使用等号
    public T selectByPrimaryKey(Object primaryKey) {
        return mapper.selectByPrimaryKey(primaryKey);
    }

    // 根据主键对象，判断该主键所表示的<T>记录是否存在
    public boolean existsWithPrimaryKey(Object primaryKey) {
        return mapper.existsWithPrimaryKey(primaryKey);
    }
    //endregion

    //region MySqlMapper
    // 批量插入，支持批量插入的数据库可以使用，例如MySQL,H2等，另外该接口限制实体包含`id`属性并且必须为自增列
    public int insertList(List<T> recordList) {
        return mySqlMapper.insertList(recordList);
    }

    // 插入数据，限制为实体包含`id`属性并且必须为自增列，实体配置的主键策略无效
    public int insertUseGeneratedKeys(T record) {
        return mySqlMapper.insertUseGeneratedKeys(record);
    }
    //endregion

    //region IdsMapper
    // 根据主键@Id进行查询，多个Id以逗号,分割
    public List<T> selectByIds(String ids) {
        return idsMapper.selectByIds(ids);
    }

    // 根据主键@Id进行删除，多个Id以逗号,分割
    public int deleteByIds(String ids) {
        return idsMapper.deleteByIds(ids);
    }
    //endregion

    //region ExampleMapper
    // 根据Example条件进行查询
    public List<T> selectByExample(Example example) {
        return mapper.selectByExample(example);
    }

    // 根据Example条件进行查询总数
    public int selectCountByExample(Example example) {
        return mapper.selectCountByExample(example);
    }

    //根据Example条件删除数据，返回删除的条数
    public int deleteByExample(Example example) {
        return mapper.deleteByExample(example);
    }

    // 根据Example条件更新实体`record`包含的全部属性，null值会被更新，返回更新的条数
    public int updateByExample(T record, Example example) {
        return mapper.updateByExample(record, example);
    }

    // 根据Example条件更新实体`record`包含的不是null的属性值，返回更新的条数
    public int updateByExampleSelective(T record, Example example) {
        return mapper.updateByExampleSelective(record, example);
    }
    //endregion

    //region 分页查询
    // 根据实体中的属性值进行查询，分页返回查询结果，查询条件使用等号
    public PageInfo<T> selectForPageList(Integer pageNum, Integer pageSize, String orderBy, T record) {
        PageHelper.startPage(pageNum, pageSize).setOrderBy(orderBy);
        return new PageInfo<>(this.select(record));
    }

    // 根据Example条件进行查询，分页返回查询结果
    public PageInfo<T> selectForPageList(Integer pageNum, Integer pageSize, String orderBy, Example example) {
        PageHelper.startPage(pageNum, pageSize).setOrderBy(orderBy);
        return new PageInfo<>(this.selectByExample(example));
    }

    // 根据入参的条件进行分页，进行绝对匹配筛选，并返回<T>对象的集合
    public List<T> selectByRowBounds(T record, RowBounds rowBounds) {
        return mapper.selectByRowBounds(record, rowBounds);
    }

    // 根据Example条件进行分页，进行绝对匹配筛选，并返回<T>对象的集合
    public List<T> selectByExampleAndRowBounds(Example example, RowBounds rowBounds) {
        return mapper.selectByExampleAndRowBounds(example, rowBounds);
    }
    //endregion

}
