package com.example.arcan.repository;

import com.example.arcan.entity.Node;
import org.springframework.data.neo4j.annotation.Query;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NodeRepository extends Neo4jRepository<Node, Long> {
    @Query("MATCH (n: Node) WHERE n.name={name} and n.projectId={projectId} RETURN n")
    Node findNodeByNameProjectId (@Param("name")String name, @Param("projectId")String projectId);

    @Query("MATCH (n: Node)-[:betweenClass]->(nn: Node) WHERE n.name={name}" +
            " and n.projectId={projectId} and nn.projectId={projectId} RETURN nn")
    List<Node> findDependencyNode(@Param("name") String name, @Param("projectId") String projectId);

    @Query("MATCH (n: Node)-[:afferent]->(nn: Node) WHERE n.name={name1} and nn.name={name2}" +
            " and n.projectId=nn.projectId={projectId} RETURN n")
    Node isExistAfferent(@Param("name1")String name1, @Param("name2")String name2, @Param("projectId")String projectId);

    @Query("MATCH (n: Node)-[:efferent]->(nn: Node) WHERE n.name={name1} and nn.name={name2}" +
            " and n.projectId=nn.projectId={projectId} RETURN n")
    Node isExistEfferent(@Param("name1")String name1, @Param("name2")String name2, @Param("projectId")String projectId);

    @Query("MATCH (n: Node)-[:membershipPackage]->(nn: Node) WHERE n.name={name} and n.projectId={projectId} RETURN nn")
    Node findParent(@Param("name") String name, @Param("projectId") String projectId);
}
