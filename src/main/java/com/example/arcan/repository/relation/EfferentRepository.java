package com.example.arcan.repository.relation;

import com.example.arcan.entity.relation.EfferentType;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface EfferentRepository extends Neo4jRepository<EfferentType, Long>{
    EfferentType findByFromNameAndToNameAndProjectId(@Param("fromName")String fromName,
                                                     @Param("toName")String toName,
                                                     @Param("projectId")String projectId);
}
