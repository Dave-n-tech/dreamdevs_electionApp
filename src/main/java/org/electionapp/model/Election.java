package org.electionapp.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Document(collection = "elections")
public class Election {
    @Id
    private String id;

    private String name;
    private String description;

    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private ElectionStatus status = ElectionStatus.UPCOMING;
}
