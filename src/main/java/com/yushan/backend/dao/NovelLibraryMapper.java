package com.yushan.backend.dao;

import com.yushan.backend.entity.NovelLibrary;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface NovelLibraryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(NovelLibrary record);

    int insertSelective(NovelLibrary record);

    NovelLibrary selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(NovelLibrary record);

    int updateByPrimaryKey(NovelLibrary record);
}