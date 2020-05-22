package com.example.arcan.controller;

import com.example.arcan.WebAppConfig;
import com.example.arcan.analysis.detection.Detector;
import com.example.arcan.analysis.sourcemodel.SM_Project;
import com.example.arcan.dao.History;
import com.example.arcan.dao.Repository;
import com.example.arcan.service.HistoryService;
import com.example.arcan.service.RepositoryService;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.kohsuke.github.GitHubBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
        System.out.println(rootFile.getName());
        System.out.println(pathFile.listFiles()[1]);
        if(rootFile.getName().equals("__MACOSX")) {
            rootFile = pathFile.listFiles()[1];
        }
        SM_Project project = new SM_Project(rootFile, projectId);
        project.readFiles();
        project.initGraph();
        project.computeClassMetrics();
        project.computePackageMetrics();

        Detector detector = new Detector(projectId);
        map.put("data", detector.detectSmells());

        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/testExist", method = RequestMethod.POST)
    public Object testExist(@RequestBody Map<String, Object> projectInfo) {
        Map<String, Object> map = new HashMap<>();

        String githubAddress = (String) projectInfo.get("githubAddress");
        boolean res = true;

        try {
            String fullName = "";
            GitHub gitHub = GitHub.connectUsingPassword("zlk-bobule", "Lcm199858");
            if(githubAddress.length()>23){
                if(githubAddress.substring(0,19).equals("https://github.com/")&&githubAddress.substring(githubAddress.length()-4,githubAddress.length()).equals(".git")){
                    fullName = githubAddress.substring(19,githubAddress.length()-4);
                    GHRepository ghRepository = gitHub.getRepository(fullName);
                    if(ghRepository==null){
                        res = false;
                    }
                    res = true;
                }else{
                    res = false;
                }
            }else {
                res = false;
            }

        } catch (IOException e) {
//             e.printStackTrace();
            res = false;
        }
        map.put("success", res);
        return map;
    }

    @ResponseBody
    @RequestMapping(value = "/analysisGithubProject", method = RequestMethod.POST)
    public Object analysisGithubProject(@RequestBody Map<String, Object> projectInfo) {
        Map<String, Object> map = new HashMap<>();

        String githubAddress = (String) projectInfo.get("githubAddress");
        String repoId = (String) projectInfo.get("repoId");

        String startStr = githubAddress.substring(19);
        String repoStr = startStr.substring(0,startStr.length()-4);
        String urlStr = "https://api.github.com/repos/"+repoStr+"/zipball/master";

        URL url = null;
        try {
            url = new URL("https://api.github.com/repos/zlk-bobule/detection/zipball/master");
            HttpsURLConnection con = (HttpsURLConnection) url.openConnection();

// Check for errors
            int responseCode = con.getResponseCode();
            InputStream inputStream;
            if (responseCode == HttpURLConnection.HTTP_OK) {
                inputStream = con.getInputStream();
            } else {
                inputStream = con.getErrorStream();
            }

            OutputStream output = new FileOutputStream("/Users/zhuyuxin/Desktop/test.zip");

// Process the response
            BufferedReader reader;
            String line = null;
            reader = new BufferedReader(new InputStreamReader(inputStream));
            while ((line = reader.readLine()) != null) {
                output.write(line.getBytes());
            }

            output.close();
            inputStream.close();
        } catch (Exception e) {
//            e.printStackTrace();
        }

        String path = WebAppConfig.BASE + "/repositories/" + "c4b55047fefd42698cf8e237b6c336cc" + "/" + "e0232c823de74cc5b17430aa8fca6312" + "/";
        File pathFile = new File(path);
        File rootFile = pathFile.listFiles()[0];
        //FileNode root = processService.process(rootFile, projectId);
        SM_Project project = new SM_Project(rootFile, "e0232c823de74cc5b17430aa8fca6312");
//        project.readFiles();
//        project.initGraph();
//        project.computeClassMetrics();
//        project.computePackageMetrics();

        Detector detector = new Detector("e0232c823de74cc5b17430aa8fca6312");
        map.put("data", detector.detectSmells());

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
