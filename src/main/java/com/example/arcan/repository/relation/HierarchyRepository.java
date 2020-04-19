package com.example.arcan.repository.relation;

import com.example.arcan.entity.relation.HierarchyType;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface HierarchyRepository extends Neo4jRepository<HierarchyType, Long>{
    HierarchyType findByFromNameAndToNameAndProjectId(@Param("fromName")String fromName,
                                        @Param("toName")String toName,
                                        @Param("projectId")String projectId);
}
