package com.yushan.backend.dao;

import com.yushan.backend.entity.Library;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LibraryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Library record);

    int insertSelective(Library record);

    Library selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Library record);

    int updateByPrimaryKey(Library record);
}