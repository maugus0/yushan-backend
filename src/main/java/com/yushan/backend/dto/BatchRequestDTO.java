package com.yushan.backend.dto;

import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class BatchRequestDTO {
    @NotEmpty(message = "ids can not be empty")
    private List<Integer> ids;

    public List<Integer> getIds() {
        return ids == null ? null : new ArrayList<>(ids);
    }

    public void setIds(List<Integer> ids) {
        this.ids = ids == null ? null : new ArrayList<>(ids);
    }
}
