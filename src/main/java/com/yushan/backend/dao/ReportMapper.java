package com.yushan.backend.dao;

import com.yushan.backend.dto.ReportSearchRequestDTO;
import com.yushan.backend.entity.Report;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.UUID;

@Mapper
public interface ReportMapper {
    // Basic CRUD operations
    int deleteByPrimaryKey(Integer id);
    int insert(Report record);
    int insertSelective(Report record);
    Report selectByPrimaryKey(Integer id);
    int updateByPrimaryKeySelective(Report record);
    int updateByPrimaryKey(Report record);

    // UUID-based selection
    Report selectByUuid(@Param("uuid") UUID uuid);

    // Search and pagination
    List<Report> selectReportsWithPagination(@Param("req") ReportSearchRequestDTO req);
    long countReports(@Param("req") ReportSearchRequestDTO req);

    // Content reports
    List<Report> selectReportsByNovelId(@Param("novelId") Integer novelId);
    List<Report> selectReportsByCommentId(@Param("commentId") Integer commentId);

    // User reports
    List<Report> selectReportsByReporterId(@Param("reporterId") UUID reporterId);

    // Status updates
    int updateReportStatus(@Param("id") Integer id, @Param("status") String status, 
                          @Param("adminNotes") String adminNotes, @Param("resolvedBy") UUID resolvedBy);

    // Check if user already reported
    boolean existsReportByUserAndContent(@Param("reporterId") UUID reporterId, @Param("contentType") String contentType, @Param("contentId") Integer contentId);
}
