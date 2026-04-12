package org.electionapp.model;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
@Document(collection = "voters")
public class Voter {
    @Id
    private String id;
    private String name;

    @Indexed(unique = true)
    private String email;

    @Indexed(unique = true)
    private UUID votingId;
}
