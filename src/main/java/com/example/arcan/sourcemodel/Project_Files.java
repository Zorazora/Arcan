package com.example.arcan.sourcemodel;

import com.example.arcan.utils.FileNode;
import com.example.arcan.utils.enums.FileType;
import lombok.Data;
import org.apache.bcel.classfile.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

@Data
public class Project_Files {
    private File rootFile;
    private FileNode root;
    private ArrayList<String> classNames;
    private ArrayList<String> packageNames;

    public Project_Files(File rootFile) {
        this.rootFile = rootFile;
        this.root = new FileNode(rootFile.getName(), FileType.PACKAGE, null, null);
        this.classNames = new ArrayList<>();
        this.packageNames = new ArrayList<>();
    }

    public void readFiles(String path, FileNode parent, String prefix) {
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
                readFiles(list[i].getPath(), packageNode, packageName);
            } else if(list[i].getName().endsWith(".class")) {
                try {
                    JavaClass javaClass = new ClassParser(list[i].getAbsolutePath()).parse();
                    String className = javaClass.getClassName();
                    classNames.add(className);
                    FileNode classNode = new FileNode(className, FileType.CLASS, javaClass, parent);
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
                    parent.addChild(classNode);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
