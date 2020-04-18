package com.example.arcan.entity.relation;

import com.example.arcan.entity.Node;
import lombok.Builder;
import lombok.Data;
import org.neo4j.ogm.annotation.*;

@Data
@Builder
@RelationshipEntity(type = "betweenClass")
public class BetweenClassType {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private Node from;

    @EndNode
    private Node to;

    @Property(name = "projectId")
    private String projectId;
}
