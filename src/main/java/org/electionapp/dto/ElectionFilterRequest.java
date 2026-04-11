package org.electionapp.dto;

import lombok.Data;
import org.electionapp.model.ElectionStatus;

import java.time.LocalDateTime;

@Data
public class ElectionFilterRequest {
    private ElectionStatus status;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    public ElectionFilterRequest(ElectionStatus status, LocalDateTime startDate, LocalDateTime endDate){
        this.status = status;
        this.startDate = startDate;
        this.endDate = endDate;
    }

}
