package org.electionapp.dto;

import lombok.Data;
import lombok.NonNull;
import org.electionapp.model.ElectionStatus;

import java.time.LocalDateTime;

@Data
public class CreateElectionRequest {
    @NonNull
    private String name;
    private String description;
    @NonNull
    private LocalDateTime startDate;
    @NonNull
    private LocalDateTime endDate;
    @NonNull
    private ElectionStatus status;

    public CreateElectionRequest() {
    }
}
