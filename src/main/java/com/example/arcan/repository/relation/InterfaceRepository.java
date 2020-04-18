package com.example.arcan.repository.relation;

import com.example.arcan.entity.relation.InterfaceType;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface InterfaceRepository extends Neo4jRepository<InterfaceType, Long>{
}
