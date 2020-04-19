package com.example.arcan.repository.relation;

import com.example.arcan.entity.relation.BetweenClassType;
import com.example.arcan.entity.relation.BetweenPackageType;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface BetweenPackageRepository extends Neo4jRepository<BetweenPackageType, Long>{
}
