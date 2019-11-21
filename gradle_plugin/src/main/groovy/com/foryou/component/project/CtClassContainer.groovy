package com.foryou.component.project

import javassist.CtClass

class CtClassContainer{

    static final String NAME_INJECT_TO_CLASS = "com.foryou.component.core.LogisticsCenter"
    static final String NAME_APP_PROXY = "com.foryou.component.AppProxy"
    static final String NAME_METHOD_INJECT = "inject"
    static final String NAME_METHOD_REGISTER = "register"

    CtClass logisticsCenter
    ArrayList<CtClass> appProxies = []
    String destPath
    ArrayList<String> classNames = []
}