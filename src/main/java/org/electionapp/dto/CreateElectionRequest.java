package org.electionapp.dto;

import lombok.Data;
import lombok.NonNull;
import org.electionapp.model.ElectionStatus;

import java.time.LocalDateTime;

@Data
public class CreateElectionRequest {
    private String name;
    private String description;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    @NonNull
    private ElectionStatus status;

    public CreateElectionRequest() {
    }
}
