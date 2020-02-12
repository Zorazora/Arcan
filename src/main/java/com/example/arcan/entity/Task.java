package com.example.arcan.entity;

import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@NodeEntity(label = "Task")
@Data
public class Task {
    @Id
    @GeneratedValue
    private Long id;

    @Property(name = "taskName")
    private String taskName;
}
