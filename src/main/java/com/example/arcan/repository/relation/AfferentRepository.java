package com.example.arcan.repository.relation;

import com.example.arcan.entity.relation.AfferentType;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface AfferentRepository extends Neo4jRepository<AfferentType, Long>{

    @Query("MATCH (a:Node),(b:Node) " +
            "WHERE a.name={fromName} AND b.name={toName} AND a.projectId=b.projectId={projectId}" +
            "CREATE UNIQUE (a)-[r: afferent{fromName: {fromName}, toName: {toName}, projectId: {projectId}}]-(b)" +
            "RETURN r")
    AfferentType saveRelationship(AfferentType afferentType);

    AfferentType findByFromNameAndToNameAndProjectId(@Param("fromName")String fromName,
                                        @Param("toName")String toName,
                                        @Param("projectId")String projectId);
}
