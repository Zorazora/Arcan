package com.example.arcan.analysis.sourcemodel;

import com.example.arcan.service.SearchService;
import com.example.arcan.utils.SpringUtil;
import lombok.Data;
import org.apache.bcel.classfile.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Data
public class SM_Class {
    private String className;
    private JavaClass javaClass;
    private String projectId;
    private int FI;
    private int FO;
    private int CBO;
    private double LCOM;

    private SearchService searchService;

    public SM_Class(String className, JavaClass javaClass, String projectId) {
        this.className = className;
        this.javaClass = javaClass;
        this.projectId = projectId;
        this.searchService = SpringUtil.getBean(SearchService.class);
    }

    public void computeMetrics() {
        computeFI();
        computeFO();
        computeCBO();
        computeLCOM();
        searchService.setClassMetrics(className,projectId,FI,FO,CBO,LCOM);
    }

    private void computeFI() {
        FI = searchService.countFanIn(className, projectId);
    }

    private void computeFO() {
        FO = searchService.countFanOut(className, projectId);
    }

    private void computeCBO() {
        int betweenClassDependency = searchService.countBetweenClass(className, projectId);
        int hierarchyDependency = searchService.countHierarchyDependency(className, projectId);
        CBO = betweenClassDependency-hierarchyDependency;
    }

    private void computeLCOM() {
        ArrayList<Set<String>> sets = new ArrayList<>();
        boolean allEmpty = true;
        for(Method method: javaClass.getMethods()) {
            Set<String> set = new HashSet<>();
            ConstantPool pool = method.getConstantPool();
            for(Constant constant: pool.getConstantPool()){
                allEmpty = false;
                if(constant instanceof ConstantFieldref) {
                    String variables = pool.constantToString(constant);
                    if(variables.endsWith("I")) {
                        variables = variables.split(" ")[0];
                        set.add(variables);
                    }
                }
            }
            sets.add(set);
        }
        if (allEmpty) {
            LCOM = 0.0;
        }
        int P = 0;
        int Q = 0;
        for(int i=0; i<sets.size()-1; i++) {
            for(int j=i+1; j<sets.size(); j++) {
                //计算交集
                Set<String> set = new HashSet<>(sets.get(i));
                set.retainAll(sets.get(j));
                if(set.size() == 0) {
                    P++;
                }else {
                    Q++;
                }
            }
        }
        if (P>Q) {
            LCOM = P-Q;
        }else {
            LCOM = 0;
        }
    }
}
