package com.yushan.backend.dao;

import com.yushan.backend.entity.Novel;
import com.yushan.backend.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface SearchMapper {

    /**
     * Search novels by keyword and optional category
     */
    List<Novel> searchNovels(@Param("keyword") String keyword,
                             @Param("category") String category,
                             @Param("offset") Integer offset,
                             @Param("pageSize") Integer pageSize,
                             @Param("sortBy") String sortBy,
                             @Param("sortOrder") String sortOrder);

    /**
     * Count total novels matching search criteria
     */
    Integer countNovels(@Param("keyword") String keyword,
                        @Param("category") String category);

    /**
     * Search users by keyword
     */
    List<User> searchUsers(@Param("keyword") String keyword,
                           @Param("offset") Integer offset,
                           @Param("pageSize") Integer pageSize,
                           @Param("sortBy") String sortBy,
                           @Param("sortOrder") String sortOrder);

    /**
     * Count total users matching search criteria
     */
    Integer countUsers(@Param("keyword") String keyword);
}
