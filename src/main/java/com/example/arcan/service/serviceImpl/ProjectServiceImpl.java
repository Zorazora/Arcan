package com.example.arcan.service.serviceImpl;

import com.example.arcan.WebAppConfig;
import com.example.arcan.analysis.sourcemodel.SM_Class;
import com.example.arcan.analysis.sourcemodel.SM_Package;
import com.example.arcan.analysis.sourcemodel.SM_Project;
import com.example.arcan.analysis.sourcemodel.reader.JarReader;
import com.example.arcan.analysis.sourcemodel.reader.ProjectReader;
import com.example.arcan.analysis.sourcemodel.reader.ZipReader;
import com.example.arcan.entity.Node;
import com.example.arcan.entity.relation.*;
import com.example.arcan.repository.NodeRepository;
import com.example.arcan.repository.relation.*;
import com.example.arcan.service.ProjectService;
import com.example.arcan.utils.enums.NodeModifier;
import com.example.arcan.utils.enums.NodeType;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;

@Service("ProjectService")
public class ProjectServiceImpl implements ProjectService{
    @Autowired
    private NodeRepository nodeRepository;
    @Autowired
    private MembershipPackageRepository membershipPackageRepository;
    @Autowired
    private InterfaceRepository interfaceRepository;
    @Autowired
    private HierarchyRepository hierarchyRepository;
    @Autowired
    private BetweenClassRepository betweenClassRepository;
    @Autowired
    private AfferentRepository afferentRepository;
    @Autowired
    private EfferentRepository efferentRepository;
    @Autowired
    private BetweenPackageRepository betweenPackageRepository;

    @Override
    public SM_Project readFiles(String repoId, String projectId) {
        ProjectReader projectReader;

        String path = WebAppConfig.BASE + "/repositories/" + repoId + "/" + projectId + "/";
        File pathFile = new File(path);
        File rootFile = pathFile.listFiles()[0];
        if(rootFile.getName().equals("__MACOSX")) {
            rootFile = pathFile.listFiles()[1];
        }
        if(rootFile.getName().endsWith(".jar")){
            projectReader = new JarReader(repoId, projectId);
            return projectReader.readFiles();
        }else if(rootFile.getName().endsWith(".zip")) {
            projectReader = new ZipReader(repoId, projectId);
            return projectReader.readFiles();
        }
        return null;
    }

    @Override
    public void initGraph(SM_Project project) {
        //创建节点
        for(String packageName: project.getPackageNames()) {
            Node node = Node.builder().projectId(project.getProjectId()).name(packageName).type(NodeType.INTERNAL.toString()).
                    modifier(NodeModifier.PACKAGE.toString()).build();
            nodeRepository.save(node);
        }
        for(SM_Class classNode: project.getClasses()) {
            String modifier = "";
            if(classNode.getJavaClass().isAbstract()) {
                modifier = NodeModifier.ABSTRACT.toString();
            }else if(classNode.getJavaClass().isInterface()) {
                modifier = NodeModifier.INTERFACE.toString();
            }else {
                modifier = NodeModifier.CLASS.toString();
            }
            Node node = Node.builder().projectId(project.getProjectId()).name(classNode.getClassName()).
                    type(NodeType.INTERNAL.toString()).modifier(modifier).build();
            nodeRepository.save(node);
            Node class1 = nodeRepository.findNodeByNameProjectId(classNode.getClassName(), project.getProjectId());
            Node parent = nodeRepository.findNodeByNameProjectId(classNode.getParent(), project.getProjectId());
            MembershipPackageType membershipPackageType = MembershipPackageType.builder().from(class1).to(parent).
                    projectId(project.getProjectId()).fromName(class1.getName()).toName(parent.getName()).build();
            membershipPackageRepository.save(membershipPackageType);
        }

        //构建依赖关系图
        for(SM_Class classNode: project.getClasses()) {
            Node class1 = nodeRepository.findNodeByNameProjectId(classNode.getClassName(), project.getProjectId());
            Node parent = nodeRepository.findNodeByNameProjectId(classNode.getParent(), project.getProjectId());
            for(String className: classNode.getJavaClass().getInterfaceNames()) {
                if(project.getClassNames().contains(className)) {
                    Node implementation = nodeRepository.findNodeByNameProjectId(className, project.getProjectId());
                    InterfaceType interfaceType = InterfaceType.builder().from(class1).to(implementation).
                            projectId(project.getProjectId()).fromName(class1.getName()).toName(implementation.getName()).build();
                    interfaceRepository.save(interfaceType);
                }
            }
            String superClassName = classNode.getJavaClass().getSuperclassName();
            if(project.getClassNames().contains(superClassName)) {
                Node superClass = nodeRepository.findNodeByNameProjectId(superClassName, project.getProjectId());
                HierarchyType hierarchyType = HierarchyType.builder().from(class1).to(superClass).
                        projectId(project.getProjectId()).fromName(class1.getName()).toName(superClass.getName()).build();
                hierarchyRepository.save(hierarchyType);
            }
            ConstantPool pool = classNode.getJavaClass().getConstantPool();
            for(Constant constant: pool.getConstantPool()) {
                if(constant instanceof ConstantClass) {
                    String usedName = pool.constantToString(constant);
                    if(!usedName.equals(classNode.getClassName()) && project.getClassNames().contains(usedName)) {
                        if(betweenClassRepository.findByFromNameAndToNameAndProjectId(class1.getName(),
                                usedName, project.getProjectId())==null) {
                            Node class2 = nodeRepository.findNodeByNameProjectId(usedName, project.getProjectId());
                            BetweenClassType betweenClassType = BetweenClassType.builder().from(class1).to(class2).
                                    projectId(project.getProjectId()).fromName(class1.getName()).toName(class2.getName()).build();
                            betweenClassRepository.save(betweenClassType);
                            Node class2_parent = nodeRepository.findParent(class2.getName(), project.getProjectId());
                            if(!parent.getName().equals(class2_parent.getName())) {
                                if(afferentRepository.findByFromNameAndToNameAndProjectId(class1.getName(),
                                        class2_parent.getName(), project.getProjectId())==null) {
                                    AfferentType afferentType = AfferentType.builder().from(class1).
                                            to(class2_parent).projectId(project.getProjectId()).
                                            fromName(class1.getName()).toName(class2_parent.getName()).build();
                                    afferentRepository.save(afferentType);
                                }
                                if(efferentRepository.findByFromNameAndToNameAndProjectId(class2.getName(),
                                        parent.getName(), project.getProjectId())==null) {
                                    EfferentType efferentType = EfferentType.builder().from(class2).
                                            to(parent).projectId(project.getProjectId()).
                                            fromName(class2.getName()).toName(parent.getName()).build();
                                    efferentRepository.save(efferentType);
                                }
                                if(afferentRepository.findByFromNameAndToNameAndProjectId(parent.getName(),
                                        class2_parent.getName(), project.getProjectId())==null) {
                                    AfferentType afferentType = AfferentType.builder().from(parent).
                                            to(class2_parent).projectId(project.getProjectId()).
                                            fromName(parent.getName()).toName(class2_parent.getName()).build();
                                    afferentRepository.save(afferentType);
                                }
                                if(efferentRepository.findByFromNameAndToNameAndProjectId(class2_parent.getName(),
                                        parent.getName(), project.getProjectId())==null) {
                                    EfferentType efferentType = EfferentType.builder().from(class2_parent).
                                            to(parent).projectId(project.getProjectId()).
                                            fromName(class2_parent.getName()).toName(parent.getName()).build();
                                    efferentRepository.save(efferentType);
                                }
                                if(betweenPackageRepository.findByFromNameAndToNameAndProjectId(parent.getName(),
                                        class2_parent.getName(), project.getProjectId())==null){
                                    BetweenPackageType betweenPackageType = BetweenPackageType.builder().from(parent).
                                            to(class2_parent).projectId(project.getProjectId()).
                                            fromName(parent.getName()).toName(class2_parent.getName()).build();
                                    betweenPackageRepository.save(betweenPackageType);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    @Override
    public void computeMetrics(SM_Project project) {
        for(SM_Class clazz: project.getClasses()) {
            clazz.computeMetrics();
        }

        for(SM_Package sm_package: project.getPackages()) {
            sm_package.computeMetrics();
        }
    }
}
