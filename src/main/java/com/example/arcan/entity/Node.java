package com.example.arcan.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@Data
@NodeEntity(label = "Node")
public class Node {
    @Id
    @GeneratedValue
    private Long id;

    @Property(name = "projectId")
    private String projectId;

    @Property(name = "name")
    private String name;

    @Property(name = "type")
    private String type;

    @Property(name = "modifier")
    private String modifier;
}
