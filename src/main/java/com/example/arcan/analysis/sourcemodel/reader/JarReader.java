package com.example.arcan.analysis.sourcemodel.reader;

import com.example.arcan.WebAppConfig;
import com.example.arcan.analysis.sourcemodel.SM_Class;
import com.example.arcan.analysis.sourcemodel.SM_Package;
import com.example.arcan.analysis.sourcemodel.SM_Project;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class JarReader extends ProjectReader{
    private String jarPath;
    private ArrayList<String> packageNames;
    private ArrayList<String> classNames;

    public JarReader(String repoId, String projectId) {
        super(repoId, projectId);
        packageNames = new ArrayList<>();
        classNames = new ArrayList<>();
    }

    private void setJarPath() {
        String path = WebAppConfig.BASE + "/repositories/" + getRepoId() + "/" + getProjectId() + "/";
        File pathFile = new File(path);
        File rootFile = pathFile.listFiles()[0];
        this.jarPath = rootFile.getAbsolutePath();
        System.out.println(jarPath);
    }

    @Override
    public SM_Project readFiles() {
        setJarPath();
        SM_Project project = new SM_Project(getProjectId());
        try {
            JarFile jarFile = new JarFile(jarPath);
            //File file = new File(jarPath);
            Enumeration<JarEntry> fileList = jarFile.entries();

            while (fileList.hasMoreElements()) {
                JarEntry jarEntry = fileList.nextElement();

                String name = jarEntry.getName().replaceAll("/",".");
                if(name.endsWith(".class")) {
                    String ultiName = name.substring(0, name.length()-6);

                    JavaClass javaClass = new ClassParser(jarFile.getInputStream(jarEntry), ultiName).parse();
                    String packageName = javaClass.getPackageName();
                    if(!packageNames.contains(packageName)){
                        packageNames.add(packageName);
                        SM_Package sm_package = new SM_Package(packageName, getProjectId());
                        project.addPackage(sm_package);
                    }
                    //System.out.println(javaClass);
                    String className = javaClass.getClassName();
                    classNames.add(className);
                    SM_Class sm_class = new SM_Class(className, javaClass, getProjectId(), packageName);
                    project.addClass(sm_class);
//                    ClassNode classNode = new ClassNode(className,javaClass,packageName);
//                    classNodes.add(classNode);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        project.setPackageNames(packageNames);
        project.setClassNames(classNames);
        return project;
    }
}
