package com.example.arcan.repository.relation;

import com.example.arcan.entity.relation.BetweenClassType;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface BetweenClassRepository extends Neo4jRepository<BetweenClassType, Long>{
}
