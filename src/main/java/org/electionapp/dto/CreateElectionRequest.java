package org.electionapp.dto;

import lombok.Data;
import lombok.NonNull;

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

    public CreateElectionRequest() {
    }
}
