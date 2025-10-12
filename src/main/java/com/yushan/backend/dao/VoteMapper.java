package com.yushan.backend.dao;

import com.yushan.backend.entity.Vote;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.UUID;

@Mapper
public interface VoteMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Vote record);

    int insertSelective(Vote record);

    Vote selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Vote record);

    int updateByPrimaryKey(Vote record);

    long countByUserId(UUID userId);

    List<Vote> selectByUserIdWithPagination(UUID userId, int offset, int limit);
}
