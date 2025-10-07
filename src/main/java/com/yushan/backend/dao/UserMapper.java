package com.yushan.backend.dao;

import com.yushan.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

import java.util.UUID;

@Mapper
public interface UserMapper {
    int deleteByPrimaryKey(UUID uuid);

    int insert(User record);

    int insertSelective(User record);

    User selectByPrimaryKey(UUID uuid);

    User selectByEmail(String email);

    List<User> selectUsersByRanking(@Param("offset") int offset,
                                    @Param("limit") int limit);

    long countAllUsers();

    long countAllAuthors();

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);
}