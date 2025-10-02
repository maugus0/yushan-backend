package com.yushan.backend.dao;

import com.yushan.backend.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper
public interface CategoryMapper {
    // Methods previously added by Zhang Yan
    int deleteByPrimaryKey(Integer id);
    int insert(Category record);
    int insertSelective(Category record);
    Category selectByPrimaryKey(Integer id);
    int updateByPrimaryKeySelective(Category record);
    int updateByPrimaryKey(Category record);
    // Methods added by Ahan Jaiswal to implement YW-55 properly
    List<Category> selectAll();
    List<Category> selectActiveCategories();
    Category selectBySlug(String slug);
}