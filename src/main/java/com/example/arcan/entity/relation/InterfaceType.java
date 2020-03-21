package com.example.arcan.entity.relation;

import com.example.arcan.entity.Node;
import lombok.Data;
import org.neo4j.ogm.annotation.*;

@Data
@RelationshipEntity( type = "interface")
public class InterfaceType {
    @Id
    @GeneratedValue
    private Long id;

    @StartNode
    private Node start;

    @EndNode
    private Node end;
}
