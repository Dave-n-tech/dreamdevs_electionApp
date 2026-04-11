package org.electionapp.dto;

import lombok.Data;
import org.electionapp.model.ElectionStatus;

import java.time.LocalDateTime;

@Data
public class CreateElectionRequest {
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private ElectionStatus status;
}
