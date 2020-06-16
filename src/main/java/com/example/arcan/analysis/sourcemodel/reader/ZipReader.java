package com.example.arcan.analysis.sourcemodel.reader;

import com.example.arcan.WebAppConfig;
import com.example.arcan.analysis.sourcemodel.SM_Class;
import com.example.arcan.analysis.sourcemodel.SM_Package;
import com.example.arcan.analysis.sourcemodel.SM_Project;
import org.apache.bcel.classfile.ClassParser;
import org.apache.bcel.classfile.JavaClass;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class ZipReader extends ProjectReader{
    private String filePath;
    private ArrayList<String> classNames;
    private ArrayList<String> packageNames;
    private File root;
    private SM_Project project;

    public ZipReader(String repoId, String projectId) {
        super(repoId, projectId);
        classNames = new ArrayList<>();
        packageNames = new ArrayList<>();
        project = new SM_Project(getProjectId());
    }

    private void setFilePath(){
        String path = WebAppConfig.BASE + "/repositories/" + getRepoId() + "/" + getProjectId() + "/";
        File pathFile = new File(path);
        File rootFile = pathFile.listFiles()[0];
        unzip(rootFile.getAbsolutePath(), path);
        for(int i=0; i<pathFile.listFiles().length; i++) {
            File tmp = pathFile.listFiles()[i];
            if(!tmp.getName().endsWith(".zip") && !tmp.getName().equals("__MACOSX")) {
                rootFile = tmp;
                this.filePath = rootFile.getPath();
                this.root = rootFile;
            }
        }
    }

    @Override
    public SM_Project readFiles() {
        setFilePath();
        readFilesRecursive(filePath);
        project.setClassNames(classNames);
        project.setPackageNames(packageNames);
        return project;
    }

    private void readFilesRecursive(String path) {
        File file = new File(path);
        File[] list = file.listFiles();
        for (int i = 0; i < list.length; i++) {
            if(list[i].isDirectory()) {
                readFilesRecursive(list[i].getPath());
            }else if(list[i].getName().endsWith(".class")) {
                try {
                    JavaClass javaClass = new ClassParser(list[i].getAbsolutePath()).parse();
                    String className = javaClass.getClassName();
                    String packageName = javaClass.getPackageName();
                    if(!packageNames.contains(packageName)) {
                        packageNames.add(packageName);
                        SM_Package sm_package = new SM_Package(packageName, getProjectId());
                        project.addPackage(sm_package);
                    }
                    classNames.add(className);
                    SM_Class sm_class = new SM_Class(className, javaClass, getProjectId(), packageName);
                    project.addClass(sm_class);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void unzip(String zipFile, String dir) {
        Expand expand = new Expand();
        expand.setSrc(new File(zipFile));
        expand.setDest(new File(dir));
        Project p = new Project();
        expand.setProject(p);
        expand.execute();
    }
}
