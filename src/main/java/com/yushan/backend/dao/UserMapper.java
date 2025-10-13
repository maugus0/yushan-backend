package com.yushan.backend.dao;

import com.yushan.backend.dto.AdminUserFilterDTO;
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

    int updateByPrimaryKeySelective(User record);

    int updateByPrimaryKey(User record);

    List<User> selectUsersForAdmin(@Param("filter") AdminUserFilterDTO filter,
                                   @Param("offset") int offset);

    long countUsersForAdmin(@Param("filter") AdminUserFilterDTO filter);

    List<User> selectAllUsersForRanking();

    List<User> selectByUuids(List<UUID> uuids);
}