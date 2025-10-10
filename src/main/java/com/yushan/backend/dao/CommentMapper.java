package com.yushan.backend.dao;

import com.yushan.backend.dto.CommentSearchRequestDTO;
import com.yushan.backend.entity.Comment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface CommentMapper {
    // Basic CRUD operations
    int deleteByPrimaryKey(Integer id);
    int insert(Comment record);
    int insertSelective(Comment record);
    Comment selectByPrimaryKey(Integer id);
    int updateByPrimaryKeySelective(Comment record);
    int updateByPrimaryKey(Comment record);

    // Select by foreign keys
    List<Comment> selectByChapterId(Integer chapterId);
    List<Comment> selectByUserId(UUID userId);
    List<Comment> selectByNovelId(Integer novelId);

    // Paginated queries
    List<Comment> selectCommentsWithPagination(CommentSearchRequestDTO searchRequest);
    List<Comment> selectCommentsByNovelWithPagination(
            @Param("novelId") Integer novelId,
            @Param("isSpoiler") Boolean isSpoiler,
            @Param("search") String search,
            @Param("sort") String sort,
            @Param("order") String order,
            @Param("page") int page,
            @Param("size") int size
    );

    // Count queries
    long countComments(CommentSearchRequestDTO searchRequest);
    long countByChapterId(Integer chapterId);
    long countByNovelId(Integer novelId);
    long countCommentsByNovel(
            @Param("novelId") Integer novelId,
            @Param("isSpoiler") Boolean isSpoiler,
            @Param("search") String search
    );

    // Like count update
    int updateLikeCount(@Param("id") Integer id, @Param("increment") Integer increment);

    // Validation/Check queries
    boolean existsByUserAndChapter(@Param("userId") UUID userId, @Param("chapterId") Integer chapterId);
}