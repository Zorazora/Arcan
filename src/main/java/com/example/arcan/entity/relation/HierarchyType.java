package com.example.arcan.entity.relation;

import com.example.arcan.entity.Node;
import lombok.Builder;
import lombok.Data;
import org.neo4j.ogm.annotation.*;

@Data
@Builder
@RelationshipEntity(type = "hierarchy")
public class HierarchyType {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private Node from;

    @EndNode
    private Node to;

    @Property(name = "projectId")
    private String projectId;

    @Property(name = "fromName")
    private String fromName;

    @Property(name = "toName")
    private String toName;
}
