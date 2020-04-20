package com.example.arcan.analysis.sourcemodel;

import com.example.arcan.entity.Node;
import com.example.arcan.entity.relation.*;
import com.example.arcan.service.SearchService;
import com.example.arcan.utils.FileNode;
import com.example.arcan.utils.SpringUtil;
import com.example.arcan.utils.enums.FileType;
import com.example.arcan.utils.enums.NodeModifier;
import com.example.arcan.utils.enums.NodeType;
import lombok.Data;
import org.apache.bcel.classfile.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Data
public class SM_Project {
    private File rootFile;
    private FileNode root;
    private ArrayList<String> classNames;
    private ArrayList<String> packageNames;
    private ArrayList<JavaClass> classes;
    private String projectId;

    private SearchService searchService;

    public SM_Project(File rootFile, String projectId) {
        this.rootFile = rootFile;
        this.root = new FileNode(rootFile.getName(), FileType.PACKAGE, null, null);
        this.classNames = new ArrayList<>();
        this.packageNames = new ArrayList<>();
        this.classes = new ArrayList<>();
        this.projectId = projectId;

        this.searchService = SpringUtil.getBean(SearchService.class);
    }

    public void readFiles() {
        readFilesRecursive(rootFile.getPath(), root, "");
    }

    private void readFilesRecursive(String path, FileNode parent, String prefix) {
        File file = new File(path);
        File[] list = file.listFiles();
        for (int i = 0; i < list.length; i++) {
            if (list[i].isDirectory()) {
                // 递归子目录
                if(!prefix.endsWith(".")&&!prefix.equals("")) {
                    prefix += ".";
                }
                String packageName = prefix+list[i].getName();
                FileNode packageNode = new FileNode(packageName, FileType.PACKAGE, null, parent);
                parent.addChild(packageNode);
                packageNames.add(packageName);
                readFilesRecursive(list[i].getPath(), packageNode, packageName);
            } else if(list[i].getName().endsWith(".class")) {
                try {
                    JavaClass javaClass = new ClassParser(list[i].getAbsolutePath()).parse();
                    String className = javaClass.getClassName();
                    classNames.add(className);
                    classes.add(javaClass);
                    FileNode classNode = new FileNode(className, FileType.CLASS, javaClass, parent);
                    parent.addChild(classNode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void initGraph() {
        List<FileNode> children = new ArrayList<>();
        children.add(root);
        createNode(children);
        createRelationship(root);
        createAfferentEfferent(root);
    }

    private void createNode(List<FileNode> children) {
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
            searchService.saveNode(node);
            thisChildren = child.getChildren();
            if(thisChildren != null && thisChildren.size()>0) {
                allChildren.addAll(thisChildren);
            }
        }
        if(allChildren.size() > 0){
            createNode(allChildren);
        }
    }

    private void createRelationship(FileNode node) {
        if (node != null) {
            if (node.getChildren() != null && !node.getChildren().isEmpty()) {
                for (FileNode item: node.getChildren()) {
                    Node child = searchService.findNodeByNameProjectId(item.getName(), projectId);
                    Node parent = searchService.findNodeByNameProjectId(node.getName(), projectId);
                    MembershipPackageType membership = MembershipPackageType.builder().from(child).to(parent).
                            projectId(projectId).fromName(child.getName()).toName(parent.getName()).build();
                    searchService.saveMembershipPackage(membership);
                    if(item.getType().equals(FileType.CLASS)) {
                        try {
                            for(String className: item.getContent().getInterfaceNames()) {
                                if(classNames.contains(className)) {
                                    Node implementation = searchService.findNodeByNameProjectId(className, projectId);
                                    InterfaceType interfaceType = InterfaceType.builder().from(child).to(implementation).
                                            projectId(projectId).fromName(child.getName()).toName(implementation.getName()).build();
                                    searchService.saveInterface(interfaceType);
                                }
                            }
                            String superClassName = item.getContent().getSuperclassName();
                            if(classNames.contains(superClassName)) {
                                Node superClass = searchService.findNodeByNameProjectId(superClassName, projectId);
                                HierarchyType hierarchyType = HierarchyType.builder().from(child).to(superClass).
                                        projectId(projectId).fromName(child.getName()).toName(superClass.getName()).build();
                                searchService.saveHierarchy(hierarchyType);
                            }
                            ConstantPool pool = item.getContent().getConstantPool();
                            for(Constant constant: pool.getConstantPool()) {
                                if(constant instanceof ConstantClass) {
                                    String usedName = pool.constantToString(constant);
                                    if(!usedName.equals(item.getName()) && classNames.contains(usedName)) {
                                        if(searchService.findByFromNameAndToNameAndProjectIdBetweenClass(child.getName(),
                                                usedName, projectId)==null) {
                                            Node dependent = searchService.findNodeByNameProjectId(usedName, projectId);
                                            BetweenClassType betweenClassType = BetweenClassType.builder().from(child).to(dependent).
                                                    projectId(projectId).fromName(child.getName()).toName(dependent.getName()).build();
                                            searchService.saveBetweenClass(betweenClassType);
                                        }
                                    }
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    createRelationship(item);
                }
            }
        }
    }

    private void createAfferentEfferent(FileNode node) {
        if(node != null) {
            if(node.getChildren()!=null && !node.getChildren().isEmpty()) {
                for(FileNode item: node.getChildren()) {
                    if(item.getType().equals(FileType.CLASS)) {
                        List<Node> nodeList = searchService.findDependencyNode(item.getName(), projectId);
                        if (nodeList != null && !nodeList.isEmpty()) {
                            for(Node class2: nodeList) {
                                Node class1 = searchService.findNodeByNameProjectId(item.getName(), projectId);
                                Node class2_parent = searchService.findParent(class2.getName(), projectId);
                                Node class1_parent = searchService.findParent(class1.getName(), projectId);
                                if(searchService.findByFromNameAndToNameAndProjectIdAfferent(class1.getName(),
                                        class2_parent.getName(), projectId)==null) {
                                    AfferentType afferentType = AfferentType.builder().from(class1).
                                            to(class2_parent).projectId(projectId).
                                            fromName(class1.getName()).toName(class2_parent.getName()).build();
                                    searchService.saveAfferent(afferentType);
                                }
                                if(searchService.findByFromNameAndToNameAndProjectIdEfferent(class2.getName(),
                                        class1_parent.getName(), projectId)==null) {
                                    EfferentType efferentType = EfferentType.builder().from(class2).
                                            to(class1_parent).projectId(projectId).
                                            fromName(class2.getName()).toName(class1_parent.getName()).build();
                                    searchService.saveEfferent(efferentType);
                                }
                                if(!class1_parent.getName().equals(class2_parent.getName())) {
                                    if(searchService.findByFromNameAndToNameAndProjectIdAfferent(class1_parent.getName(),
                                            class2_parent.getName(), projectId)==null) {
                                        AfferentType afferentType = AfferentType.builder().from(class1_parent).
                                                to(class2_parent).projectId(projectId).
                                                fromName(class1_parent.getName()).toName(class2_parent.getName()).build();
                                        searchService.saveAfferent(afferentType);
                                    }
                                    if(searchService.findByFromNameAndToNameAndProjectIdEfferent(class2_parent.getName(),
                                            class1_parent.getName(), projectId)==null) {
                                        EfferentType efferentType = EfferentType.builder().from(class2_parent).
                                                to(class1_parent).projectId(projectId).
                                                fromName(class2_parent.getName()).toName(class1_parent.getName()).build();
                                        searchService.saveEfferent(efferentType);
                                    }
                                    if(searchService.findByFromNameAndToNameAndProjectIdBetweenPackage(class1_parent.getName(),
                                            class2_parent.getName(), projectId)==null){
                                        BetweenPackageType betweenPackageType = BetweenPackageType.builder().from(class1_parent).
                                                to(class2_parent).projectId(projectId).
                                                fromName(class1_parent.getName()).toName(class2_parent.getName()).build();
                                        searchService.saveBetweenPackage(betweenPackageType);
                                    }
                                }
                            }
                        }
                    }
                    createAfferentEfferent(item);
                }
            }
        }
    }

    public void computeClassMetrics() {
        SM_Class clazz;
        for(int i=0; i<this.classNames.size(); i++) {
            clazz = new SM_Class(classNames.get(i), classes.get(i), projectId);
            clazz.computeMetrics();
        }
    }

    public void computePackageMetrics() {
        SM_Package sm_package;
        for(String packageName: packageNames) {
            sm_package = new SM_Package(packageName, projectId);
            sm_package.computeMetrics();
        }
    }

}
