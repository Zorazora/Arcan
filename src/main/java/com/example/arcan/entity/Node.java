package com.example.arcan.entity;

import lombok.Builder;
import lombok.Data;
import org.neo4j.ogm.annotation.GeneratedValue;
import org.neo4j.ogm.annotation.Id;
import org.neo4j.ogm.annotation.NodeEntity;
import org.neo4j.ogm.annotation.Property;

@Data
@Builder
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

    @Property(name = "FI")
    private int FI;
    @Property(name = "FO")
    private int FO;
    @Property(name = "CBO")
    private int CBO;
    @Property(name = "LCOM")
    private double LCOM;

    @Property(name = "CA")
    private int CA;
    @Property(name = "CE")
    private int CE;
    @Property(name = "RMI")
    private double RMI;
    @Property(name = "RMA")
    private double RMA;
    @Property(name = "RMD")
    private double RMD;
}
