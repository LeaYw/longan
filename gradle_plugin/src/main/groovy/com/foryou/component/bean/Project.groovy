package com.foryou.component.bean

/**
 * Description:
 * Created by liyawei
 * Date:2019-08-13 17:07
 * Email:liyawei@foryou56.com
 */

public class Project {
    String name
    String groupId
    String artifactId
    String url
    String build_project


    @Override
    public String toString() {
        return "Project{" +
                "name='" + name + '\'' +
                ", groupId='" + groupId + '\'' +
                ", artifactId='" + artifactId + '\'' +
                ", url='" + url + '\'' +
                ", build_project='" + build_project + '\'' +
                '}';
    }
}