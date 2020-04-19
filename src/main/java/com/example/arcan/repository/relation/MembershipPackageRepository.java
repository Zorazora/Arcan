package com.example.arcan.repository.relation;

import com.example.arcan.entity.relation.MembershipPackageType;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface MembershipPackageRepository extends Neo4jRepository<MembershipPackageType, Long>{
    MembershipPackageType findByFromNameAndToNameAndProjectId(@Param("fromName")String fromName,
                                                              @Param("toName")String toName,
                                                              @Param("projectId")String projectId);
}
