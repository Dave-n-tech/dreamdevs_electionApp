package org.electionapp.repository;

import org.electionapp.model.Vote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface VoteRepository extends MongoRepository<Vote, String> {
    Vote findByVoterIdAndElectionId(UUID voterId, String electionId);
    List<Vote> findByElectionId(String electionId);
    List<Vote> findByCandidateId(String candidateId);
}
