package com.example.arcan.repository.relation;

import com.example.arcan.entity.relation.EfferentType;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface EfferentRepository extends Neo4jRepository<EfferentType, Long>{
}
