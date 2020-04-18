package com.example.arcan.repository;

import com.example.arcan.entity.Node;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NodeRepository extends Neo4jRepository<Node, Long> {
    @Query("MATCH (n: Node) WHERE n.name={name} and n.projectId={projectId} RETURN n")
    Node findNodeByNameProjectId (@Param("name")String name, @Param("projectId")String projectId);
}
