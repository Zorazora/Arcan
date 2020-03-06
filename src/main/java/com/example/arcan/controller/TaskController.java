package com.example.arcan.controller;

import com.example.arcan.dao.User;
import com.example.arcan.entity.Task;
import com.example.arcan.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.example.arcan.repository.TaskRepository;

import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/task")
public class TaskController {

    @Autowired
    TaskRepository taskRepository;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @Transactional
    public Task saveTask(@RequestBody Task taskInfo) {
//        Task task = new Task();
//        task.setTaskName(taskName);
//        System.out.println(taskName);
        return taskRepository.save(taskInfo);
    }

    @RequestMapping(value = "/{name}", method = RequestMethod.GET)
    @ResponseBody
    public Task create(@PathVariable String name) {
        Task task = taskRepository.findByTaskName(name);
        return task;
    }

    @RequestMapping(value = "/try", method = RequestMethod.GET)
    public String test(){
//        String id = UUID.randomUUID().toString();
//        String mailaddress = "jsrgzzh715@163.com";
//        String username = "Zora";
//        String password = "123456";
//        User user = new User(id, mailaddress, username, password);
//        System.out.println(userService.insertUser(user));
        System.out.println("Connecting Right! Happy!");
        return "Successfully";
    }
}
