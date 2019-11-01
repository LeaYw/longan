package com.foryou.component.bean

/**
 * Description:
 * Created by liyawei
 * Date:2019-08-13 17:07
 * Email:liyawei@foryou56.com
 */

public class DebuggingProjects {
    Project target
    List<Project> dependencies = new ArrayList<>()


    @Override
    public String toString() {
        return "DebuggingProjects{" +
                "target=" + target +
                ", dependencies=" + dependencies.toListString() +
                '}';
    }
}