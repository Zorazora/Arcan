package com.example.arcan.repository.relation;

import com.example.arcan.entity.relation.HierarchyType;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface HierarchyRepository extends Neo4jRepository<HierarchyType, Long>{
}
