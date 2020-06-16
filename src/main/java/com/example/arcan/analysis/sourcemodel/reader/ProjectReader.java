package com.example.arcan.analysis.sourcemodel.reader;

import com.example.arcan.analysis.sourcemodel.SM_Project;
import lombok.Data;

@Data
public abstract class ProjectReader {
    private String projectId;
    private String repoId;

    public ProjectReader(String repoId, String projectId) {
        this.repoId = repoId;
        this.projectId = projectId;
    }

    abstract public SM_Project readFiles();
}
