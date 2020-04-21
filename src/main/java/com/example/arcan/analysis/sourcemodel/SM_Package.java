package com.example.arcan.analysis.sourcemodel;

import com.example.arcan.service.SearchService;
import com.example.arcan.utils.SpringUtil;
import lombok.Data;

@Data
public class SM_Package {
    private String packageName;
    private String projectId;
    private int CA;
    private int CE;
    private double RMI;
    private double RMA;
    private double RMD;
    private SearchService searchService;

    public SM_Package(String packageName, String projectId) {
        this.packageName = packageName;
        this.projectId = projectId;
        this.searchService = SpringUtil.getBean(SearchService.class);
    }

    public void computeMetrics() {
        computeCA();
        computeCE();
        computeRMI();
        computeRMA();
        computeRMD();
        searchService.setPackageMetrics(packageName, projectId, CA, CE, RMI, RMA, RMD);
    }

    private void computeCA() {
        this.CA = searchService.countCa(packageName, projectId);
    }

    private void computeCE() {
        this.CE = searchService.countCe(packageName,projectId);
    }

    private void computeRMI() {
        this.RMI = (double) CE/(CA+CE);
    }

    private void computeRMA() {
        this.RMA = searchService.calculateRMA(packageName, projectId);
    }

    private void computeRMD() {
        this.RMD = Math.abs(this.RMA+this.RMI-1);
    }
}
