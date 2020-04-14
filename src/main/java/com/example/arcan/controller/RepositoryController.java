package com.example.arcan.controller;

import com.example.arcan.WebAppConfig;
import com.example.arcan.dao.History;
import com.example.arcan.dao.Repository;
import com.example.arcan.service.HistoryService;
import com.example.arcan.service.ProcessService;
import com.example.arcan.service.RepositoryService;
import com.example.arcan.utils.FileNode;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/repository")
public class RepositoryController {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ProcessService processService;

    @ResponseBody
    @RequestMapping(value = "/create", method = RequestMethod.POST)
    public Object createRepository(@RequestBody Map<String, Object> repoInfo) {
        Map<String, Object> map = new HashMap<>();

        String userId = (String) repoInfo.get("userId");
        String repoName = (String) repoInfo.get("repoName");
        String description = (String) repoInfo.get("description");

        map.put("data", repositoryService.createRepository(userId,repoName,description));

        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/list/{userId}", method = RequestMethod.GET)
    public Object getRepositoryList(@PathVariable("userId") String userId) {
        Map<String, Object> map = new HashMap<>();

        map.put("data", repositoryService.getRepositoryList(userId));

        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/project/{repoId}", method = RequestMethod.GET)
    public Object getRecentProject(@PathVariable("repoId") String repoId) {
        Map<String, Object> map = new HashMap<>();

        Repository repository = repositoryService.getRepositoryById(repoId);

        map.put("status", repository.getStatus());

        if(!repository.getStatus().equals("CREATED")) {
            History history = historyService.getRecent(repoId);
            map.put("data", history);
            String path = WebAppConfig.BASE + "/repositories/" + repoId + "/" + history.getProjectId();
            File file = new File(path);
            if(file.isDirectory()) {
                File[] files = file.listFiles();
                map.put("name", files[0].getName());
            }
        }

        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/delete/{repoId}", method = RequestMethod.GET)
    public Object deleteRepository(@PathVariable("repoId") String repoId) {
        Map<String, Object> map = new HashMap<>();

        map.put("data", repositoryService.deleteRepository(repoId));

        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/upload/{repoId}", method = RequestMethod.POST)
    public Object uploadProject(@RequestParam(value = "file") MultipartFile file,
                                @PathVariable("repoId") String repoId) {
        Map<String, Object> map = new HashMap<>();

        String projectId = UUID.randomUUID().toString().replace("-", "");

        String path = WebAppConfig.BASE + "/zip/" + projectId + "/";
        String filename = file.getOriginalFilename();
        File newFile = new File(path, filename);

        if(!newFile.getParentFile().exists()) {
            newFile.getParentFile().mkdirs();
        }

        try {
            if(newFile.createNewFile()) {
                file.transferTo(new File(newFile.getAbsolutePath()));
            }

            path = WebAppConfig.BASE + "/repositories/" + repoId + "/" + projectId +"/";
            File pathFile = new File(path);

            if(!pathFile.getParentFile().exists()) {
                pathFile.getParentFile().mkdirs();
            }

            if(!pathFile.exists()) {
                pathFile.mkdirs();
            }

            unzip(newFile.getAbsolutePath(), path);

            historyService.createHistory(repoId, projectId);
            repositoryService.modifyStatus(repoId, "UPLOADED");

            map.put("success", true);
            map.put("data", projectId);

        } catch (IOException e) {
            e.printStackTrace();
            map.put("success", false);
            map.put("data", e.getStackTrace());
        }

        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/analysis", method = RequestMethod.POST)
    public Object analysisProject(@RequestBody Map<String, Object> projectInfo) {
        Map<String, Object> map = new HashMap<>();

        String repoId = (String) projectInfo.get("repoId");
        String projectId = (String) projectInfo.get("projectId");

        String path = WebAppConfig.BASE + "/repositories/" + repoId + "/" + projectId + "/";
        File pathFile = new File(path);
        File rootFile = pathFile.listFiles()[0];
        FileNode root = processService.process(rootFile, projectId);

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
}
