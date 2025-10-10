package com.yushan.backend.dao;

import com.yushan.backend.entity.History;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface HistoryMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(History record);

    int insertSelective(History record);

    History selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(History record);

    int updateByPrimaryKey(History record);

    History selectByUserAndNovel(@Param("userId") UUID userId,
                                 @Param("novelId") Integer novelId);

    List<History> selectByUserIdWithPagination(@Param("userId") UUID userId,
                                               @Param("offset") int offset,
                                               @Param("size") int size);

    long countByUserId(@Param("userId") UUID userId);

    int deleteByUserId(@Param("userId") UUID userId);
}