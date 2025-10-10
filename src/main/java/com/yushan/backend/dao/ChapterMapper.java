package com.yushan.backend.dao;

import com.yushan.backend.entity.Chapter;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface ChapterMapper {
    // Basic CRUD operations, already existing from Yan's initial setup.
    int deleteByPrimaryKey(Integer id);
    int insert(Chapter record);
    int insertSelective(Chapter record);
    Chapter selectByPrimaryKey(Integer id);
    int updateByPrimaryKeySelective(Chapter record);
    int updateByPrimaryKey(Chapter record);

    List<Chapter> selectByIds(List<Integer> ids);
    
    // UUID-based selection for public APIs
    Chapter selectByUuid(@Param("uuid") UUID uuid);
    // Novel-specific chapter queries
    List<Chapter> selectByNovelId(@Param("novelId") Integer novelId);
    List<Chapter> selectPublishedByNovelId(@Param("novelId") Integer novelId);
    List<Chapter> selectByNovelIdWithPagination(@Param("novelId") Integer novelId,
                                                @Param("offset") int offset,
                                                @Param("limit") int limit);
    List<Chapter> selectPublishedByNovelIdWithPagination(@Param("novelId") Integer novelId,
                                                         @Param("offset") int offset,
                                                         @Param("limit") int limit);
    long countByNovelId(@Param("novelId") Integer novelId);
    long countPublishedByNovelId(@Param("novelId") Integer novelId);
    // Chapter navigation
    Chapter selectNextChapter(@Param("novelId") Integer novelId,
                              @Param("chapterNumber") Integer chapterNumber);
    Chapter selectPreviousChapter(@Param("novelId") Integer novelId,
                                  @Param("chapterNumber") Integer chapterNumber);
    // Specific chapter selection
    Chapter selectByNovelIdAndChapterNumber(@Param("novelId") Integer novelId,
                                            @Param("chapterNumber") Integer chapterNumber);
    // View count management
    int incrementViewCount(@Param("id") Integer id);
    // Chapter existence checks
    boolean existsByNovelIdAndChapterNumber(@Param("novelId") Integer novelId,
                                            @Param("chapterNumber") Integer chapterNumber);
    // Get max chapter number for a novel
    Integer selectMaxChapterNumberByNovelId(@Param("novelId") Integer novelId);
    // Batch operations
    int batchInsert(@Param("chapters") List<Chapter> chapters);
    // Soft delete
    int softDeleteByPrimaryKey(@Param("id") Integer id);
    int softDeleteByUuid(@Param("uuid") UUID uuid);
    // Author/Admin queries - get drafts
    List<Chapter> selectDraftsByNovelId(@Param("novelId") Integer novelId);
    List<Chapter> selectScheduledByNovelId(@Param("novelId") Integer novelId);
    // Statistics
    long sumWordCountByNovelId(@Param("novelId") Integer novelId);
    // Bulk status updates
    int updatePublishStatusByIds(@Param("ids") List<Integer> ids,
                                 @Param("isValid") Boolean isValid);
}