package com.yushan.backend.dto;

import com.yushan.backend.enums.UserStatus;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminUserFilterDTO {
    // Pagination
    private int page = 0;
    private int size = 20;

    // Filters
    private UserStatus status;
    private Boolean isAdmin;
    private Boolean isAuthor;

    // Sorting
    private String sortBy = "createTime";
    private String sortOrder = "desc";
}