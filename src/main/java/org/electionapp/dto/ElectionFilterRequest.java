package org.electionapp.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ElectionFilterRequest {
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public ElectionFilterRequest(LocalDateTime startDate, LocalDateTime endDate){
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
