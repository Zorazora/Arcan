package com.example.arcan.controller;

import com.example.arcan.WebAppConfig;
import com.example.arcan.service.ProcessService;
import com.example.arcan.utils.FileNode;
import com.example.arcan.utils.enums.FileType;
import org.apache.tomcat.util.bcel.classfile.ClassParser;
import org.apache.tomcat.util.bcel.classfile.JavaClass;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/project")
public class ProjectController {
    @Autowired
    private ProcessService processService;

    @RequestMapping(value = "/read", method = RequestMethod.POST)
    public Object readProject(@RequestParam(value = "file") MultipartFile file) {
        Map<String, Object> map = new HashMap<>();
        System.out.println("GET FILE");

        String uuid = UUID.randomUUID().toString().replace("-", "");

        String path = WebAppConfig.BASE + "/zip/" + uuid + "/";
        String filename = file.getOriginalFilename();
        File newFile = new File(path, filename);

        if(!newFile.getParentFile().exists()) {
            newFile.getParentFile().mkdirs();
        }

        try {
            if(newFile.createNewFile()) {
                file.transferTo(new File(newFile.getAbsolutePath()));
            }

            path = WebAppConfig.BASE + "/projects/" + uuid + "/";
            File pathFile = new File(path);
            if(!pathFile.exists()) {
                pathFile.mkdirs();
            }

            unzip(newFile.getAbsolutePath(), path);

            File rootFile = pathFile.listFiles()[0];
            FileNode root = processService.process(rootFile);
//            FileNode root = new FileNode(rootFile.getName(), FileType.PACKAGE, null);
//            readProject(rootFile.getPath(), root);

            map.put("success", true);
            map.put("data", path);

        } catch (IOException e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("data", e.getStackTrace());
        }

        return map;
    }

    private static void unzip(String zipFile, String dir) {
        Expand expand = new Expand();
        expand.setSrc(new File(zipFile));
        expand.setDest(new File(dir));
        Project p = new Project();
        expand.setProject(p);
        expand.execute();
    }

//    private static boolean readProject(String path, FileNode parent) {
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
//                    FileNode classNode = new FileNode(list[i].getName(), FileType.CLASS, new ClassParser(new FileInputStream(list[i])).parse());
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
