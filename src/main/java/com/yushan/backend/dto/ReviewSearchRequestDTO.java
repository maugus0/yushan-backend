package com.yushan.backend.dto;

import lombok.Data;

@Data
public class ReviewSearchRequestDTO {
    private Integer page = 0;
    private Integer size = 10;
    private String sort = "createTime";
    private String order = "desc";
    private Integer novelId;
    private Integer rating;
    private Boolean isSpoiler;
    private String search;

    public ReviewSearchRequestDTO() {
    }

    public ReviewSearchRequestDTO(Integer page, Integer size, String sort, String order, 
                                 Integer novelId, Integer rating, Boolean isSpoiler, String search) {
        this.page = page;
        this.size = size;
        this.sort = sort;
        this.order = order;
        this.novelId = novelId;
        this.rating = rating;
        this.isSpoiler = isSpoiler;
        this.search = search;
    }
}
