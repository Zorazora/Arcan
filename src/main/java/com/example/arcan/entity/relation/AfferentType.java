package com.example.arcan.entity.relation;

import com.example.arcan.entity.Node;
import lombok.Data;
import org.neo4j.ogm.annotation.*;

@Data
@RelationshipEntity(type = "afferent")
public class AfferentType {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private Node from;

    @EndNode
    private Node to;
}
