package org.electionapp.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Document(collection = "votes")
public class Vote {
    @Id
    private String id;
    private UUID voterId;
    private String candidateId;
    private String electionId;
    private LocalDateTime timestamp;
}