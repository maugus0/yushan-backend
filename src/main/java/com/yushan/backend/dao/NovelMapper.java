package com.yushan.backend.dao;

import com.yushan.backend.dto.NovelSearchRequestDTO;
import com.yushan.backend.entity.Novel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface NovelMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Novel record);

    int insertSelective(Novel record);

    Novel selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Novel record);

    int updateByPrimaryKey(Novel record);
    
    // Pagination and filtering methods
    List<Novel> selectNovelsWithPagination(@Param("req") NovelSearchRequestDTO req);
    
    long countNovels(@Param("req") NovelSearchRequestDTO req);
}