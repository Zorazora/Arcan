package com.example.arcan.repository.relation;

import com.example.arcan.entity.relation.BetweenPackageType;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;

public interface BetweenPackageRepository extends Neo4jRepository<BetweenPackageType, Long>{
    @Query("MATCH (a:Node),(b:Node) " +
            "WHERE a.name={fromName} AND b.name={toName} AND a.projectId=b.projectId={projectId}" +
            "CREATE UNIQUE (a)-[r: betweenPackage{fromName: {fromName}, toName: {toName}, projectId: {projectId}}]-(b)" +
            "RETURN r")
    BetweenPackageType saveRelationship(BetweenPackageType betweenPackageType);

    BetweenPackageType findByFromNameAndToNameAndProjectId(@Param("fromName")String fromName,
                                                           @Param("toName")String toName,
                                                           @Param("projectId")String projectId);
}
