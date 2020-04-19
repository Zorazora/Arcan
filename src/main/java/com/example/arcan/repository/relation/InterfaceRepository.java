package com.example.arcan.repository.relation;

import com.example.arcan.entity.relation.InterfaceType;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface InterfaceRepository extends Neo4jRepository<InterfaceType, Long>{
    InterfaceType findByFromNameAndToNameAndProjectId(@Param("fromName")String fromName,
                                        @Param("toName")String toName,
                                        @Param("projectId")String projectId);
}
