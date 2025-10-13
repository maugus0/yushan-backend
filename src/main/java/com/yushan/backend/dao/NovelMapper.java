package com.yushan.backend.dao;

import com.yushan.backend.dto.AuthorResponseDTO;
import com.yushan.backend.dto.NovelSearchRequestDTO;
import com.yushan.backend.entity.Novel;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

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
    
    // Admin methods (including ARCHIVED novels)
    List<Novel> selectAllNovelsWithPagination(@Param("req") NovelSearchRequestDTO req);
    
    long countAllNovels(@Param("req") NovelSearchRequestDTO req);
    
    // Vote count methods
    int incrementVoteCount(@Param("novelId") Integer novelId);
    
    int decrementVoteCount(@Param("novelId") Integer novelId);
    
    // View count methods
    int incrementViewCount(@Param("novelId") Integer novelId);

    List<AuthorResponseDTO> selectAuthorsByRanking(@Param("sortType") String sortType,
                                                   @Param("offset") int offset,
                                                   @Param("limit") int limit);

    List<Novel> selectByIds(List<Integer> ids);

    List<Novel> selectAllNovelsForRanking();

    List<AuthorResponseDTO> selectAuthorsByUuids(List<UUID> uuids);
}