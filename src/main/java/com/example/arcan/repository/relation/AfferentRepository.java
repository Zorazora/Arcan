package com.example.arcan.repository.relation;

import com.example.arcan.entity.relation.AfferentType;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface AfferentRepository extends Neo4jRepository<AfferentType, Long>{
}
