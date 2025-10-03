package com.yushan.backend.dao;

import com.yushan.backend.entity.NovelLibrary;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface NovelLibraryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(NovelLibrary record);

    int insertSelective(NovelLibrary record);

    NovelLibrary selectByPrimaryKey(Integer id);

    NovelLibrary selectByUserIdAndNovelId(@Param("userId") UUID userId, @Param("novelId") Integer novelId);

    int updateByPrimaryKeySelective(NovelLibrary record);

    int updateByPrimaryKey(NovelLibrary record);

    // Pagination methods
    List<NovelLibrary> selectByUserIdWithPagination(@Param("userId") UUID userId,
                                                    @Param("offset") int offset,
                                                    @Param("size") int size,
                                                    @Param("sort") String sort,
                                                    @Param("order") String order);

    long countByUserId(@Param("userId") UUID userId);

    int deleteByUserIdAndNovelIds(@Param("userId") UUID userId, @Param("novelIds") List<Integer> novelIds);
}