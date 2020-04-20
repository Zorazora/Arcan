package com.example.arcan.service.serviceImpl;

import com.example.arcan.entity.Node;
import com.example.arcan.entity.relation.*;
import com.example.arcan.repository.NodeRepository;
import com.example.arcan.repository.relation.*;
import com.example.arcan.service.SearchService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service("SearchService")
public class SearchServiceImpl implements SearchService{
    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private AfferentRepository afferentRepository;
    @Autowired
    private BetweenClassRepository betweenClassRepository;
    @Autowired
    private BetweenPackageRepository betweenPackageRepository;
    @Autowired
    private EfferentRepository efferentRepository;
    @Autowired
    private HierarchyRepository hierarchyRepository;
    @Autowired
    private InterfaceRepository interfaceRepository;
    @Autowired
    private MembershipPackageRepository membershipPackageRepository;

    @Override
    public Node findNodeByNameProjectId(String name, String projectId) {
        return nodeRepository.findNodeByNameProjectId(name, projectId);
    }

    @Override
    public List<Node> findDependencyNode(String name, String projectId) {
        return nodeRepository.findDependencyNode(name, projectId);
    }

    @Override
    public Node findParent(String name, String projectId) {
        return nodeRepository.findParent(name, projectId);
    }

    @Override
    public void saveNode(Node node) {
        nodeRepository.save(node);
    }

    @Override
    public AfferentType findByFromNameAndToNameAndProjectIdAfferent(String fromName, String toName, String projectId) {
        return afferentRepository.findByFromNameAndToNameAndProjectId(fromName,toName,projectId);
    }

    @Override
    public BetweenClassType findByFromNameAndToNameAndProjectIdBetweenClass(String fromName, String toName, String projectId) {
        return betweenClassRepository.findByFromNameAndToNameAndProjectId(fromName,toName,projectId);
    }

    @Override
    public BetweenPackageType findByFromNameAndToNameAndProjectIdBetweenPackage(String fromName, String toName, String projectId) {
        return betweenPackageRepository.findByFromNameAndToNameAndProjectId(fromName,toName,projectId);
    }

    @Override
    public EfferentType findByFromNameAndToNameAndProjectIdEfferent(String fromName, String toName, String projectId) {
        return efferentRepository.findByFromNameAndToNameAndProjectId(fromName,toName,projectId);
    }

    @Override
    public HierarchyType findByFromNameAndToNameAndProjectIdHierarchy(String fromName, String toName, String projectId) {
        return hierarchyRepository.findByFromNameAndToNameAndProjectId(fromName,toName,projectId);
    }

    @Override
    public InterfaceType findByFromNameAndToNameAndProjectIdInterface(String fromName, String toName, String projectId) {
        return interfaceRepository.findByFromNameAndToNameAndProjectId(fromName,toName,projectId);
    }

    @Override
    public MembershipPackageType findByFromNameAndToNameAndProjectIdMembershipPackage(String fromName, String toName, String projectId) {
        return membershipPackageRepository.findByFromNameAndToNameAndProjectId(fromName,toName,projectId);
    }

    @Override
    public void saveAfferent(AfferentType afferentType) {
        afferentRepository.save(afferentType);
    }

    @Override
    public void saveBetweenClass(BetweenClassType betweenClassType) {
        betweenClassRepository.save(betweenClassType);
    }

    @Override
    public void saveBetweenPackage(BetweenPackageType betweenPackageType) {
        betweenPackageRepository.save(betweenPackageType);
    }

    @Override
    public void saveEfferent(EfferentType efferentType) {
        efferentRepository.save(efferentType);
    }

    @Override
    public void saveHierarchy(HierarchyType hierarchyType) {
        hierarchyRepository.save(hierarchyType);
    }

    @Override
    public void saveInterface(InterfaceType interfaceType) {
        interfaceRepository.save(interfaceType);
    }

    @Override
    public void saveMembershipPackage(MembershipPackageType membershipPackageType) {
        membershipPackageRepository.save(membershipPackageType);
    }

    @Override
    public int countFanIn(String name, String projectId) {
        return nodeRepository.countFanIn(name, projectId);
    }

    @Override
    public int countFanOut(String name, String projectId) {
        return nodeRepository.countFanOut(name, projectId);
    }

    @Override
    public int countBetweenClass(String name, String projectId) {
        return nodeRepository.countBetweenClass(name, projectId);
    }

    @Override
    public int countHierarchyDependency(String name, String projectId) {
        return nodeRepository.countHierarchyDependency(name, projectId);
    }

    @Override
    public Node setClassMetrics(String name, String projectId, int FI, int FO, int CBO, double LCOM) {
        return nodeRepository.setClassMetrics(name, projectId, FI, FO, CBO, LCOM);
    }

    @Override
    public Node setPackageMetrics(String name, String projectId, int CA, int CE, double RMI, double RMA, double RMD) {
        return nodeRepository.setPackageMetrics(name, projectId, CA, CE, RMI, RMA, RMD);
    }
}
