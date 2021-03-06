package com.example.arcan.service;

import com.example.arcan.entity.Node;
import com.example.arcan.entity.relation.*;

import java.util.List;

public interface SearchService {
    Node findNodeByNameProjectId(String name, String projectId);
    List<Node> findDependencyNode(String name, String projectId);
    Node findParent(String name, String projectId);
    void saveNode(Node node);

    AfferentType findByFromNameAndToNameAndProjectIdAfferent(String fromName, String toName, String projectId);
    BetweenClassType findByFromNameAndToNameAndProjectIdBetweenClass(String fromName, String toName, String projectId);
    BetweenPackageType findByFromNameAndToNameAndProjectIdBetweenPackage(String fromName, String toName, String projectId);
    EfferentType findByFromNameAndToNameAndProjectIdEfferent(String fromName, String toName, String projectId);
    HierarchyType findByFromNameAndToNameAndProjectIdHierarchy(String fromName, String toName, String projectId);
    InterfaceType findByFromNameAndToNameAndProjectIdInterface(String fromName, String toName, String projectId);
    MembershipPackageType findByFromNameAndToNameAndProjectIdMembershipPackage(String fromName, String toName, String projectId);

    void saveAfferent(AfferentType afferentType);
    void saveBetweenClass(BetweenClassType betweenClassType);
    void saveBetweenPackage(BetweenPackageType betweenPackageType);
    void saveEfferent(EfferentType efferentType);
    void saveHierarchy(HierarchyType hierarchyType);
    void saveInterface(InterfaceType interfaceType);
    void saveMembershipPackage(MembershipPackageType membershipPackageType);

    int countFanIn(String name, String projectId);
    int countFanOut(String name, String projectId);
    int countBetweenClass(String name, String projectId);
    int countHierarchyDependency(String name, String projectId);

    Node setClassMetrics(String name, String projectId, int FI, int FO, int CBO, double LCOM);
    Node setPackageMetrics(String name, String projectId, int CA, int CE, double RMI, double RMA, double RMD);

    int countCa(String name, String projectId);
    int countCe(String name, String projectId);
    double calculateRMA(String name, String projectId);
}
