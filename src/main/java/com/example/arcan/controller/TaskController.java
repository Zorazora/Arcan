package com.example.arcan.controller;

import com.example.arcan.entity.Task;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import com.example.arcan.repository.TaskRepository;

@RestController
@CrossOrigin
@RequestMapping("/task")
public class TaskController {

    @Autowired
    TaskRepository taskRepository;

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
        System.out.println("Connecting Right!");
        return "Successfully";
    }
}
