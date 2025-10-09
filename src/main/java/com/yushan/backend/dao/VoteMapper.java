package com.yushan.backend.dao;

import com.yushan.backend.entity.Vote;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.UUID;

@Mapper
public interface VoteMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(Vote record);

    int insertSelective(Vote record);

    Vote selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(Vote record);

    int updateByPrimaryKey(Vote record);
    
    // Custom methods for voting functionality
    Vote selectByUserAndNovel(@Param("userId") UUID userId, @Param("novelId") Integer novelId);
    
    Vote selectActiveByUserAndNovel(@Param("userId") UUID userId, @Param("novelId") Integer novelId);
    
    int deactivateVote(@Param("userId") UUID userId, @Param("novelId") Integer novelId);
    
    int reactivateVote(@Param("userId") UUID userId, @Param("novelId") Integer novelId);
    
    int countActiveVotesByNovel(@Param("novelId") Integer novelId);
    
    int incrementVoteCount(@Param("novelId") Integer novelId);
    
    int decrementVoteCount(@Param("novelId") Integer novelId);
}
