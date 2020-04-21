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

    @Query("MATCH (n: Node)-[:membershipPackage]->(nn: Node) WHERE n.name={name} and n.projectId={projectId} RETURN nn")
    Node findParent(@Param("name") String name, @Param("projectId") String projectId);

    @Query("MATCH (n: Node)-[r]->(nn: Node) WHERE nn.name={name} AND " +
            "nn.projectId={projectId} AND n.projectId={projectId} AND n.modifier<>\'PACKAGE\' RETURN count(n)")
    int countFanIn(@Param("name")String name, @Param("projectId")String projectId);

    @Query("MATCH (n: Node)-[r]->(nn: Node) WHERE n.name={name} AND " +
            "nn.projectId={projectId} AND n.projectId={projectId} AND nn.modifier<>\'PACKAGE\' RETURN count(nn)")
    int countFanOut(@Param("name")String name, @Param("projectId") String projectId);

    @Query("MATCH (n: Node)-[:betweenClass]->(nn:Node) WHERE n.name={name} AND" +
            " n.projectId={projectId} AND nn.projectId={projectId} RETURN count(nn)")
    int countBetweenClass(@Param("name")String name, @Param("projectId")String projectId);

    @Query("MATCH (n: Node)-[:hierarchy]->(nn:Node) WHERE n.name={name} AND " +
            "n.projectId={projectId} AND nn.projectId={projectId} RETURN count(nn)")
    int countHierarchyDependency(@Param("name")String name, @Param("projectId")String projectId);

    @Query("MATCH (n: Node) WHERE n.name={name} AND n.projectId={projectId} " +
            "SET n.FI={FI}, n.FO={FO}, n.CBO={CBO}, n.LCOM={LCOM} RETURN n")
    Node setClassMetrics(@Param("name")String name, @Param("projectId")String projectId,
                         @Param("FI")int FI, @Param("FO")int FO, @Param("CBO") int CBO, @Param("LCOM") double LCOM);

    @Query("MATCH (n: Node) WHERE n.name={name} AND n.projectId={projectId} " +
            "SET n.CE={CE}, n.CA={CA}, n.RMI={RMI}, n.RMA={RMA}, n.RMD={RMD} RETURN n")
    Node setPackageMetrics(@Param("name")String name, @Param("projectId")String projectId,
                           @Param("CA")int CA, @Param("CE")int CE,
                           @Param("RMI")double RMI, @Param("RMA")double RMA, @Param("RMD")double RMD);

    @Query("MATCH (start: Node)-[:membershipPackage*]->(end: Node) WHERE end.name={name} And" +
            " end.projectId={projectId} And start.projectId={projectId} And start.modifier<>\'PACKAGE\' RETURN start")
    List<Node> findInternalClasses(@Param("name") String name, @Param("projectId") String projectId);

    @Query("MATCH (n: Node) WHERE n.projectId={projectId} And n.modifier<>'PACKAGE' RETURN n")
    List<Node> findAllClassesByProjectId(@Param("projectId") String projectId);

    List<Node> findNodesByProjectIdAndModifier (@Param("projectId")String projectId, @Param("modifier")String modifier );

    @Query("MATCH (n: Node)-[:afferent]->(nn: Node) WHERE n.name={name} AND" +
            " n.projectId={projectId} AND nn.projectId={projectId} and nn.modifier=\'PACKAGE\' RETURN nn")
    List<Node> getAfferentPackage(@Param("name")String name, @Param("projectId")String projectId);
}
