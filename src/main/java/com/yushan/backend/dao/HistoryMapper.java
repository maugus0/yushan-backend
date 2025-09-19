package com.yushan.backend.dao;

import com.yushan.backend.entity.History;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface HistoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(History record);

    int insertSelective(History record);

    History selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(History record);

    int updateByPrimaryKey(History record);
}