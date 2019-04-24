package me.leayw.longan

import javassist.CtClass

class CtClassContainer{

    static final String NAME_INJECT_TO_CLASS = "me.leayw.longan_api.core.LogisticsCenter"
    static final String NAME_APP_PROXY = "me.leayw.longan_api.AppProxy"
    static final String NAME_METHOD_INJECT = "inject"
    static final String NAME_METHOD_REGISTER = "register"

    CtClass logisticsCenter
    ArrayList<CtClass> appProxies = []
    String destPath
    ArrayList<String> classNames = []
}