package com.example.arcan.service;

import com.example.arcan.utils.FileNode;

import java.io.File;

public interface ProcessService {
    FileNode process(File rootFile, String projectId);
}
