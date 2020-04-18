package com.example.arcan.service.serviceImpl;

import com.example.arcan.entity.Node;
import com.example.arcan.entity.relation.BetweenClassType;
import com.example.arcan.entity.relation.HierarchyType;
import com.example.arcan.entity.relation.InterfaceType;
import com.example.arcan.entity.relation.MembershipPackageType;
import com.example.arcan.repository.NodeRepository;
import com.example.arcan.repository.relation.BetweenClassRepository;
import com.example.arcan.repository.relation.HierarchyRepository;
import com.example.arcan.repository.relation.InterfaceRepository;
import com.example.arcan.repository.relation.MembershipPackageRepository;
import com.example.arcan.service.ProcessService;
import com.example.arcan.sourcemodel.Project_Files;
import com.example.arcan.utils.FileNode;
import com.example.arcan.utils.enums.FileType;
import com.example.arcan.utils.enums.NodeModifier;
import com.example.arcan.utils.enums.NodeType;
import org.apache.bcel.classfile.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service("processService")
public class ProcessServiceImpl implements ProcessService{

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

    private static ArrayList<String> classNames;

    @Override
    public FileNode process(File rootFile, String projectId) {
        Project_Files project = new Project_Files(rootFile);
        FileNode root = project.getRoot();
        project.readFiles(rootFile.getPath(), root, "");
        classNames = project.getClassNames();
        List<FileNode> children = new ArrayList<>();
        children.add(root);
        initGraph(children, projectId);
        createRelationship(root, projectId);
        return root;
    }

    //BFS
    public void initGraph(List<FileNode> children, String projectId) {
        List<FileNode> thisChildren, allChildren = new ArrayList<>();
        for (FileNode child: children) {
            String modifier = "";
            if(child.getType().toString().equals("PACKAGE")) {
                modifier = NodeModifier.PACKAGE.toString();
            }else if(child.getContent().isAbstract()) {
                modifier = NodeModifier.ABSTRACT.toString();
            }else if(child.getContent().isInterface()) {
                modifier = NodeModifier.INTERFACE.toString();
            }else {
                modifier = NodeModifier.CLASS.toString();
            }
            Node node = Node.builder().projectId(projectId).name(child.getName()).
                    type(NodeType.INTERNAL.toString()).modifier(modifier).build();
            nodeRepository.save(node);
            thisChildren = child.getChildren();
            if(thisChildren != null && thisChildren.size()>0) {
                allChildren.addAll(thisChildren);
            }
        }
        if(allChildren.size() > 0){
            initGraph(allChildren, projectId);
        }
    }

    //DFS
    public void createRelationship(FileNode node, String projectId){
        if (node != null) {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                for (FileNode item: node.getChildren()) {
                    Node child = nodeRepository.findNodeByNameProjectId(item.getName(), projectId);
                    Node parent = nodeRepository.findNodeByNameProjectId(node.getName(), projectId);
                    MembershipPackageType membership = MembershipPackageType.builder().from(child).to(parent).projectId(projectId).build();
                    membershipPackageRepository.save(membership);
                    if(item.getType().equals(FileType.CLASS)) {
                        try {
                            for(String className: item.getContent().getInterfaceNames()) {
                                if(classNames.contains(className)) {
                                    Node implementation = nodeRepository.findNodeByNameProjectId(className, projectId);
                                    InterfaceType interfaceType = InterfaceType.builder().from(child).to(implementation).projectId(projectId).build();
                                    interfaceRepository.save(interfaceType);
                                }
                            }
                            String superClassName = item.getContent().getSuperclassName();
                            if(classNames.contains(superClassName)) {
                                Node superClass = nodeRepository.findNodeByNameProjectId(superClassName, projectId);
                                HierarchyType hierarchyType = HierarchyType.builder().from(child).to(superClass).projectId(projectId).build();
                                hierarchyRepository.save(hierarchyType);
                            }
                            ConstantPool pool = item.getContent().getConstantPool();
                            for(Constant constant: pool.getConstantPool()) {
                                if(constant instanceof ConstantClass) {
                                    String usedName = pool.constantToString(constant);
                                    if(!usedName.equals(item.getName()) && classNames.contains(usedName)) {
                                        Node dependent = nodeRepository.findNodeByNameProjectId(usedName, projectId);
                                        BetweenClassType betweenClassType = BetweenClassType.builder().from(child).to(dependent).projectId(projectId).build();
                                        betweenClassRepository.save(betweenClassType);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    createRelationship(item, projectId);
                }
            }
        }
    }

    public void createEfferentAfferent() {

    }

//    private boolean readProject(String path, FileNode parent) {
//        File file = new File(path);
//        File[] list = file.listFiles();
//        for (int i = 0; i < list.length; i++) {
//            if (list[i].isDirectory()) {
//                // 递归子目录
//                FileNode packageNode = new FileNode(list[i].getName(), FileType.PACKAGE, null);
//                parent.addChild(packageNode);
//                readProject(list[i].getPath(), packageNode);
//            } else if(list[i].getName().endsWith(".class")) {
//                try {
//                    FileNode classNode = new FileNode(list[i].getName(), FileType.CLASS, new ClassParser(list[i].getAbsolutePath()).parse());
//                    ConstantPool pool = classNode.getContent().getConstantPool();
//                    String className = classNode.getContent().getClassName();
//                    for(Constant constant: pool.getConstantPool()) {
//                        if(constant instanceof ConstantClass) {
//                            String usedName = pool.constantToString(constant);
//                            if(!className.equals(usedName)) {
//                                System.out.println("usedName:"+usedName+" className:"+className);
//                            }
//                        }
//                    }
//                    parent.addChild(classNode);
//                } catch (IOException e) {
//                    e.printStackTrace();
//                    return false;
//                }
//            }
//        }
//        return  true;
//    }
}
