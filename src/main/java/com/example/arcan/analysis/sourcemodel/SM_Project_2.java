package com.example.arcan.analysis.sourcemodel;

import com.example.arcan.entity.Node;
import com.example.arcan.entity.relation.*;
import com.example.arcan.service.SearchService;
import com.example.arcan.utils.ClassNode;
import com.example.arcan.utils.SpringUtil;
import com.example.arcan.utils.enums.NodeModifier;
import com.example.arcan.utils.enums.NodeType;
import lombok.Data;
import org.apache.bcel.classfile.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

@Data
public class SM_Project_2 {
    private ArrayList<String> packageNames;
    private ArrayList<String> classNames;
    private ArrayList<ClassNode> classNodes;
    private String projectId;
    private String jarPath;

    private SearchService searchService;

    public SM_Project_2(String jarPath, String projectId){
        this.jarPath = jarPath;
        this.projectId = projectId;
        this.packageNames = new ArrayList<>();
        this.classNames = new ArrayList<>();
        this.classNodes = new ArrayList<>();

        this.searchService = SpringUtil.getBean(SearchService.class);
    }

    public void readFiles() {
        try {
            JarFile jarFile = new JarFile(jarPath);
            File file = new File(jarPath);
            Enumeration<JarEntry> fileList = jarFile.entries();

            while (fileList.hasMoreElements()) {
                JarEntry jarEntry = fileList.nextElement();

                String name = jarEntry.getName().replaceAll("/",".");
                if(name.endsWith(".class")) {
                    String ultiName = name.substring(0, name.length()-6);
//                    Class<?> cls = loader.loadClass(ultiName);
//                    String packageName = cls.getPackage().getName();
//                    if(!packageNames.contains(packageName)){
//                        packageNames.add(packageName);
//                    }

                    //JavaClass javaClass = new ClassParser(cls.getResourceAsStream("/"+jarEntry.getName()),name).parse();

                    JavaClass javaClass = new ClassParser(jarFile.getInputStream(jarEntry), ultiName).parse();
                    String packageName = javaClass.getPackageName();
                    if(!packageNames.contains(packageName)){
                        packageNames.add(packageName);
                    }
                    //System.out.println(javaClass);
                    String className = javaClass.getClassName();
                    classNames.add(className);
                    ClassNode classNode = new ClassNode(className,javaClass,packageName);
                    classNodes.add(classNode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
//        catch (ClassNotFoundException e) {
//            e.printStackTrace();
//        }
    }

    public void createNode() {
        for(String packageName: packageNames) {
            Node node = Node.builder().projectId(projectId).name(packageName).type(NodeType.INTERNAL.toString()).
                    modifier(NodeModifier.PACKAGE.toString()).build();
            searchService.saveNode(node);
        }
        for(ClassNode classNode: classNodes) {
            String modifier = "";
            if(classNode.getContent().isAbstract()) {
                modifier = NodeModifier.ABSTRACT.toString();
            }else if(classNode.getContent().isInterface()) {
                modifier = NodeModifier.INTERFACE.toString();
            }else {
                modifier = NodeModifier.CLASS.toString();
            }
            Node node = Node.builder().projectId(projectId).name(classNode.getName()).
                    type(NodeType.INTERNAL.toString()).modifier(modifier).build();
            searchService.saveNode(node);
            Node class1 = searchService.findNodeByNameProjectId(classNode.getName(), projectId);
            Node parent = searchService.findNodeByNameProjectId(classNode.getParent(), projectId);
            MembershipPackageType membershipPackageType = MembershipPackageType.builder().from(class1).to(parent).
                    projectId(projectId).fromName(class1.getName()).toName(parent.getName()).build();
            searchService.saveMembershipPackage(membershipPackageType);
        }
    }

    public void initGraph() {
        for(ClassNode classNode: classNodes) {
            Node class1 = searchService.findNodeByNameProjectId(classNode.getName(), projectId);
            Node parent = searchService.findNodeByNameProjectId(classNode.getParent(), projectId);
            for(String className: classNode.getContent().getInterfaceNames()) {
                if(classNames.contains(className)) {
                    Node implementation = searchService.findNodeByNameProjectId(className, projectId);
                    InterfaceType interfaceType = InterfaceType.builder().from(class1).to(implementation).
                            projectId(projectId).fromName(class1.getName()).toName(implementation.getName()).build();
                    searchService.saveInterface(interfaceType);
                }
            }
            String superClassName = classNode.getContent().getSuperclassName();
            if(classNames.contains(superClassName)) {
                Node superClass = searchService.findNodeByNameProjectId(superClassName, projectId);
                HierarchyType hierarchyType = HierarchyType.builder().from(class1).to(superClass).
                        projectId(projectId).fromName(class1.getName()).toName(superClass.getName()).build();
                searchService.saveHierarchy(hierarchyType);
            }
            ConstantPool pool = classNode.getContent().getConstantPool();
            for(Constant constant: pool.getConstantPool()) {
                if(constant instanceof ConstantClass) {
                    String usedName = pool.constantToString(constant);
                    if(!usedName.equals(classNode.getName()) && classNames.contains(usedName)) {
                        if(searchService.findByFromNameAndToNameAndProjectIdBetweenClass(class1.getName(),
                                usedName, projectId)==null) {
                            Node class2 = searchService.findNodeByNameProjectId(usedName, projectId);
                            BetweenClassType betweenClassType = BetweenClassType.builder().from(class1).to(class2).
                                    projectId(projectId).fromName(class1.getName()).toName(class2.getName()).build();
                            searchService.saveBetweenClass(betweenClassType);
                            Node class2_parent = searchService.findParent(class2.getName(), projectId);
                            if(!parent.getName().equals(class2_parent.getName())) {
                                if(searchService.findByFromNameAndToNameAndProjectIdAfferent(class1.getName(),
                                        class2_parent.getName(), projectId)==null) {
                                    AfferentType afferentType = AfferentType.builder().from(class1).
                                            to(class2_parent).projectId(projectId).
                                            fromName(class1.getName()).toName(class2_parent.getName()).build();
                                    searchService.saveAfferent(afferentType);
                                }
                                if(searchService.findByFromNameAndToNameAndProjectIdEfferent(class2.getName(),
                                        parent.getName(), projectId)==null) {
                                    EfferentType efferentType = EfferentType.builder().from(class2).
                                            to(parent).projectId(projectId).
                                            fromName(class2.getName()).toName(parent.getName()).build();
                                    searchService.saveEfferent(efferentType);
                                }
                                if(searchService.findByFromNameAndToNameAndProjectIdAfferent(parent.getName(),
                                        class2_parent.getName(), projectId)==null) {
                                    AfferentType afferentType = AfferentType.builder().from(parent).
                                            to(class2_parent).projectId(projectId).
                                            fromName(parent.getName()).toName(class2_parent.getName()).build();
                                    searchService.saveAfferent(afferentType);
                                }
                                if(searchService.findByFromNameAndToNameAndProjectIdEfferent(class2_parent.getName(),
                                        parent.getName(), projectId)==null) {
                                    EfferentType efferentType = EfferentType.builder().from(class2_parent).
                                            to(parent).projectId(projectId).
                                            fromName(class2_parent.getName()).toName(parent.getName()).build();
                                    searchService.saveEfferent(efferentType);
                                }
                                if(searchService.findByFromNameAndToNameAndProjectIdBetweenPackage(parent.getName(),
                                        class2_parent.getName(), projectId)==null){
                                    BetweenPackageType betweenPackageType = BetweenPackageType.builder().from(parent).
                                            to(class2_parent).projectId(projectId).
                                            fromName(parent.getName()).toName(class2_parent.getName()).build();
                                    searchService.saveBetweenPackage(betweenPackageType);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    public void computeMetrics() {
        SM_Class clazz;
        for(ClassNode classNode: classNodes) {
            clazz = new SM_Class(classNode.getName(), classNode.getContent(), projectId);
            clazz.computeMetrics();
        }

        SM_Package sm_package;
        for(String packageName: packageNames) {
            sm_package = new SM_Package(packageName, projectId);
            sm_package.computeMetrics();
        }
    }
}
