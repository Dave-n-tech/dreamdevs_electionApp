package org.electionapp.repository;

import org.electionapp.model.Candidate;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CandidateRepository extends MongoRepository<Candidate, String> {
    List<Candidate> findByElectionId(String electionId);
    boolean existsByNameAndElectionId(String name, String electionId);
}
