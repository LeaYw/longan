package me.leayw.longan;

import groovy.lang.Closure;

/**
 * Description:
 * Created by liyawei
 * Date:2019-08-13 17:07
 * Email:liyawei@foryou56.com
 */
public class LonganExtension {

    Closure dynamicDependencies;

    void dynamicDependencies(Closure closure) {
        this.dynamicDependencies = closure;
    }

    @Override
    public String toString() {
        return "LonganExtension{" +
                "dynamicDependencies=" + dynamicDependencies +
                '}';
    }
}
