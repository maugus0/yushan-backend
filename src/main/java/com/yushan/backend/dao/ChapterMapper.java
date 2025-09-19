package com.yushan.backend.dao;

import com.yushan.backend.entity.Chapter;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface ChapterMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Chapter record);

    int insertSelective(Chapter record);

    Chapter selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Chapter record);

    int updateByPrimaryKey(Chapter record);
}