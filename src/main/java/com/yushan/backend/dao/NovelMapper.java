package com.yushan.backend.dao;

import com.yushan.backend.dto.AuthorResponseDTO;
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

    // Ranking methods
    List<Novel> selectNovelsByRanking(@Param("categoryId") Integer categoryId,
                                      @Param("sortType") String sortType,
                                      @Param("offset") int offset,
                                      @Param("limit") int limit);

    long countNovelsByRanking(@Param("categoryId") Integer categoryId);

    List<AuthorResponseDTO> selectAuthorsByRanking(@Param("sortType") String sortType,
                                                   @Param("offset") int offset,
                                                   @Param("limit") int limit);
}