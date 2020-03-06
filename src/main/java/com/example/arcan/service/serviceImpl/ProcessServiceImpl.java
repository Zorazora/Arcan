package com.example.arcan.service.serviceImpl;

import com.example.arcan.service.ProcessService;
import com.example.arcan.utils.FileNode;
import com.example.arcan.utils.enums.FileType;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.Constant;
import org.apache.bcel.classfile.ConstantClass;
import org.apache.bcel.classfile.ConstantPool;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

@Service("processService")
public class ProcessServiceImpl implements ProcessService{
    @Override
    public FileNode process(File rootFile) {
        FileNode root = new FileNode(rootFile.getName(), FileType.PACKAGE, null);
        readProject(rootFile.getPath(), root);

        return root;
    }

    private boolean readProject(String path, FileNode parent) {
        File file = new File(path);
        File[] list = file.listFiles();
        for (int i = 0; i < list.length; i++) {
            if (list[i].isDirectory()) {
                // 递归子目录
                FileNode packageNode = new FileNode(list[i].getName(), FileType.PACKAGE, null);
                parent.addChild(packageNode);
                readProject(list[i].getPath(), packageNode);
            } else if(list[i].getName().endsWith(".class")) {
                try {
                    FileNode classNode = new FileNode(list[i].getName(), FileType.CLASS, new ClassParser(list[i].getAbsolutePath()).parse());
                    ConstantPool pool = classNode.getContent().getConstantPool();
                    String className = classNode.getContent().getClassName();
                    for(Constant constant: pool.getConstantPool()) {
                        if(constant instanceof ConstantClass) {
                            String usedName = pool.constantToString(constant);
                            if(!className.equals(usedName)) {
                                System.out.println("usedName:"+usedName+" className:"+className);
                            }
                        }
                    }
                    parent.addChild(classNode);
                } catch (IOException e) {
                    e.printStackTrace();
                    return false;
                }
            }
        }
        return  true;
    }
}
