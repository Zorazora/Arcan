package com.example.arcan.service.serviceImpl;

import com.example.arcan.entity.Node;
import com.example.arcan.repository.NodeRepository;
import com.example.arcan.service.ProcessService;
import com.example.arcan.sourcemodel.Project_Files;
import com.example.arcan.utils.FileNode;
import com.example.arcan.utils.enums.FileType;
import com.example.arcan.utils.enums.NodeModifier;
import com.example.arcan.utils.enums.NodeType;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
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

    @Override
    public FileNode process(File rootFile, String projectId) {
        Project_Files project = new Project_Files(rootFile);
        FileNode root = project.getRoot();
        project.readFiles(rootFile.getPath(), root, "");
        List<FileNode> children = new ArrayList<>();
        children.add(root);
        initGraph(children, projectId);
        return root;
    }

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
            Node node = new Node();
            node.setProjectId(projectId);
            node.setName(child.getName());
            node.setType(NodeType.INTERNAL.toString());
            node.setModifier(modifier);
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
