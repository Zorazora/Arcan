package com.example.arcan.controller;

import com.example.arcan.WebAppConfig;
import com.example.arcan.analysis.detection.Detector;
import com.example.arcan.analysis.sourcemodel.SM_Project;
import com.example.arcan.dao.History;
import com.example.arcan.dao.Repository;
import com.example.arcan.service.HistoryService;
import com.example.arcan.service.ProjectService;
import com.example.arcan.service.RepositoryService;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.taskdefs.Expand;
import org.kohsuke.github.GHRelease;
import org.kohsuke.github.GHRepository;
import org.kohsuke.github.GitHub;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@RestController
@CrossOrigin
@RequestMapping("/repository")
public class RepositoryController {
    @Autowired
    private RepositoryService repositoryService;

    @Autowired
    private HistoryService historyService;

    @Autowired
    private ProjectService projectService;

    private String LOGIN = "zlk-bobule";
    private String PASSWORD = "Lcm199858";

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
    @RequestMapping(value = "/findList", method = RequestMethod.POST)
    public Object findFitKeyWord(@RequestBody Map<String, Object> projectInfo) {
        String userId = (String) projectInfo.get("userId");
        String keyword = (String) projectInfo.get("keyword");

        Map<String, Object> map = new HashMap<>();

        System.out.println(keyword);
        List<Repository> repositories = repositoryService.findFitKeyWord(userId,keyword);
        System.out.println("个数: " + repositories.size());
        map.put("data", repositories);
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
    @RequestMapping(value = "/HistoryProjectRes/{repoId}", method = RequestMethod.GET)
    public Object getHistoryProjectRes(@PathVariable("repoId") String repoId) {
        Map<String, Object> map = new HashMap<>();

        List<History> histories = historyService.getHistoryListByRepoId(repoId);
        List<History> analysisedHistories = new ArrayList<>();//只展示分析过的
        List<String> projectNames = new ArrayList<>();
        List<Map<String, Object>> results = new ArrayList<>();
        for(History history : histories){
            File file1 =new File("results/"+history.getResultId()+".dat");
            if(file1.exists()){
                analysisedHistories.add(history);
                String path = WebAppConfig.BASE + "/repositories/" + repoId + "/" + history.getProjectId();
                File file = new File(path);
                if(file.isDirectory()) {
                    File[] files = file.listFiles();
                    projectNames.add(files[0].getName());
                }


                Object temp=null;

                FileInputStream in;
                try {
                    in = new FileInputStream(file1);
                    ObjectInputStream objIn=new ObjectInputStream(in);
                    temp=objIn.readObject();
                    objIn.close();
                    System.out.println("read object success!");
                } catch (IOException e) {
                    System.out.println("read object failed");
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
//            Detector detector = new Detector(history.getProjectId());
//            results.add(detector.detectSmells());
                results.add((HashMap<String,Object>)temp);
            }

        }

        map.put("histories", analysisedHistories);
        map.put("projectNames", projectNames);
        map.put("results", results);
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

        //String path = WebAppConfig.BASE + "/zip/" + projectId + "/";
        String path = WebAppConfig.BASE + "/repositories/" + repoId + "/" + projectId +"/";
        String filename = file.getOriginalFilename();
        System.out.println(filename);
        File newFile = new File(path, filename);

        if(!newFile.getParentFile().exists()) {
            newFile.getParentFile().mkdirs();
        }
        try {
            if(newFile.createNewFile()) {
                file.transferTo(new File(newFile.getAbsolutePath()));
            }

//            path = WebAppConfig.BASE + "/repositories/" + repoId + "/" + projectId +"/";
//            File pathFile = new File(path);
//
//            if(!pathFile.getParentFile().exists()) {
//                pathFile.getParentFile().mkdirs();
//            }
//
//            if(!pathFile.exists()) {
//                pathFile.mkdirs();
//            }
//
//            unzip(newFile.getAbsolutePath(), path);

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


//        String path = WebAppConfig.BASE + "/repositories/" + repoId + "/" + projectId + "/";
//        File pathFile = new File(path);
//        File rootFile = pathFile.listFiles()[0];
//        if(rootFile.getName().equals("__MACOSX")) {
//            rootFile = pathFile.listFiles()[1];
//        }
//        SM_Project_1 project = new SM_Project_1(rootFile, projectId);

//        String path = WebAppConfig.BASE + "/repositories/" + repoId + "/" + projectId + "/";
//        File pathFile = new File(path);
//        File rootFile = pathFile.listFiles()[0];
//        if(rootFile.getName().equals("__MACOSX")) {
//            rootFile = pathFile.listFiles()[1];
//        }
//        SM_Project_1 project = new SM_Project_1(rootFile, projectId);
//        project.readFiles();
//        project.initGraph();
//        project.computeClassMetrics();
//        project.computePackageMetrics();
//
//        Detector detector = new Detector(projectId);
//        map.put("data", detector.detectSmells());

//        String jarPath = "/Users/zorazora/Desktop/毕业课题/data/maven-core-3.0.5.jar/";
//        SM_Project_2 project = new SM_Project_2(jarPath, projectId);
//
//        project.readFiles();
//        project.createNode();
//        project.initGraph();
//        project.computeMetrics();
//
//        System.out.println(project.getPackageNames().size()+" "+project.getClassNames().size());
//        map.put("NOP", project.getPackageNames().size());
//        map.put("NOC", project.getClassNames().size());

        SM_Project project = projectService.readFiles(repoId, projectId);

        projectService.initGraph(project);
        projectService.computeMetrics(project);

        Detector detector = new Detector(projectId);

        Map<String,Object> result = detector.detectSmells();
        History history = historyService.getRecent(repoId);

        File file =new File("results/"+history.getResultId()+".dat");
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut=new ObjectOutputStream(out);
            objOut.writeObject(result);
            objOut.flush();
            objOut.close();
            System.out.println("write object success!");
        } catch (IOException e) {
            System.out.println("write object failed");
            e.printStackTrace();
        }


        map.put("data", result);
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
            GitHub gitHub = GitHub.connectUsingPassword(LOGIN, PASSWORD);
            if(githubAddress.length()>23){
                if(githubAddress.substring(0,19).equals("https://github.com/")&&githubAddress.substring(githubAddress.length()-4,githubAddress.length()).equals(".git")){
                    fullName = githubAddress.substring(19,githubAddress.length()-4);
                    GHRepository ghRepository = gitHub.getRepository(fullName);
                    List<GHRelease> releases = ghRepository.getReleases();
                    List<String> releaseNames = new ArrayList<>();
                    for(GHRelease ghRelease:releases){
                        releaseNames.add(ghRelease.getTagName());
                        System.out.println(ghRelease.getTagName());
                    }
                    if(releases.size()!=0){
                        map.put("releases",releaseNames);
                    }else{
                        map.put("releases",null);
                    }
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
    @RequestMapping(value = "/downloadRelease", method = RequestMethod.POST)
    public Object downloadRelease(@RequestBody Map<String, Object> projectInfo) throws IOException{
        Map<String, Object> map = new HashMap<>();

        String githubAddress = (String) projectInfo.get("githubAddress");
        String repoId = (String) projectInfo.get("repoId");
        String release = (String) projectInfo.get("release");
        String projectId = UUID.randomUUID().toString().replace("-", "");

        System.out.println("跑到这了");

        String urlStr = githubAddress.substring(0,githubAddress.length()-4) + "/releases/download/" + release + "/" + release + ".jar";

        URL url = new URL(urlStr);
        InputStream is = null;
        OutputStream os = null;
        try {
            String path = WebAppConfig.BASE + "/repositories/" + repoId + "/" + projectId +"/";
            File pathFile = new File(path);
            if(!pathFile.getParentFile().exists()) {
                pathFile.getParentFile().mkdirs();
            }

            if(!pathFile.exists()) {
                pathFile.mkdirs();
            }

            os = new FileOutputStream(new File(WebAppConfig.BASE + "/repositories/" + repoId + "/" + projectId +"/"+release+".jar"));
            is = url.openStream();
            // Read bytes...
            int a = 0;
            while((a = is.read()) != -1){
                os.write(a);
            }

            historyService.createHistory(repoId, projectId);

            map.put("success", true);
            map.put("data", projectId);

            System.out.println("download success");

        } catch (IOException exp) {
            exp.printStackTrace();

            map.put("success", false);
            map.put("data", exp.getStackTrace());

            System.out.println("download failed");
        } finally {
            try {
                is.close();
            } catch (Exception exp) {
            }
            try {
                os.close();
            } catch (Exception exp) {
            }
        }

        return map;
    }



//    @ResponseBody
//    @RequestMapping(value = "/analysisRelease", method = RequestMethod.POST)
//    public Object analysisRelease(@RequestBody Map<String, Object> projectInfo) throws IOException{
//        Map<String, Object> map = new HashMap<>();
//
////        String githubAddress = (String) projectInfo.get("githubAddress");
//        String repoId = (String) projectInfo.get("repoId");
//        String release = (String) projectInfo.get("release");
//        String projectId = (String) projectInfo.get("projectId");
//
////        String urlStr = githubAddress.substring(0,githubAddress.length()-4) + "/releases/download/" + release + "/" + release + ".jar";
////        System.out.println("url: "+urlStr);
////
////        URL url = new URL(urlStr);
////        InputStream is = null;
////        OutputStream os = null;
////        try {
////            String path = WebAppConfig.BASE + "/zip/" + projectId + "/";
////            File pathFile = new File(path);
////            if(!pathFile.getParentFile().exists()) {
////                pathFile.getParentFile().mkdirs();
////            }
////
////            if(!pathFile.exists()) {
////                pathFile.mkdirs();
////            }
////
////            os = new FileOutputStream(new File(WebAppConfig.BASE + "/zip/" + projectId + "/"+release+".jar"));
////            is = url.openStream();
////            // Read bytes...
////            int a = 0;
////            while((a = is.read()) != -1){
////                os.write(a);
////            }
////
////        } catch (IOException exp) {
////            exp.printStackTrace();
////        } finally {
////            try {
////                is.close();
////            } catch (Exception exp) {
////            }
////            try {
////                os.close();
////            } catch (Exception exp) {
////            }
////        }
//
//
//        String path = WebAppConfig.BASE + "/zip/" + projectId + "/"+release+".jar/";
////        String path ="/Users/zhuyuxin/Desktop/未命名文件夹/jtravis-2.1.jar";
//        SM_Project project = new SM_Project_2(path, projectId);
//        project.readFiles();
//        project.createNode();
//        project.initGraph();
//        project.computeMetrics();
//
//
//        Detector detector = new Detector(projectId);
//        map.put("data", detector.detectSmells());
//
//        return map;
//
//
//    }


    @ResponseBody
    @RequestMapping(value = "/analysisGithubProject", method = RequestMethod.POST)
    public Object analysisGithubProject(@RequestBody Map<String, Object> projectInfo) {
        Map<String, Object> map = new HashMap<>();

        String githubAddress = (String) projectInfo.get("githubAddress");
        String repoId = (String) projectInfo.get("repoId");

        String startStr = githubAddress.substring(19);
        String repoStr = startStr.substring(0,startStr.length()-4);
        String urlStr = "https://api.github.com/repos/"+repoStr+"/zipball/master";

        System.out.println(urlStr);

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

            OutputStream output = new FileOutputStream("/Users/zhuyuxin/Desktop/未命名文件");

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
        //SM_Project_1 project = new SM_Project_1(rootFile, "e0232c823de74cc5b17430aa8fca6312");
//        project.readFiles();
//        project.initGraph();
//        project.computeClassMetrics();
//        project.computePackageMetrics();

        Detector detector = new Detector("e0232c823de74cc5b17430aa8fca6312");
//        map.put("data", detector.detectSmells());

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
