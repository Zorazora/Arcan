package com.example.arcan.repository.relation;

import com.example.arcan.entity.relation.MembershipPackageType;
import org.springframework.data.neo4j.repository.Neo4jRepository;

public interface MembershipPackageRepository extends Neo4jRepository<MembershipPackageType, Long>{
}
