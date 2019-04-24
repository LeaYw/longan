# Longan
一个帮助开发者快速组件化开发的 gradle插件
## 功能介绍：
1. 组件单独运行
2. 修改资源引用，支持单独运行的同时避免资源文件打入正式包。
3. 依赖的业务组件打包时动态引入，强制低耦合，高内聚
4. 统一version
5. 限定resourcePrefix，结合Android Studio提示开发者注意资源前缀，避免资源冲突。
6. 依赖组件动态引入支持远程maven项目
7. ApplicationProxy实现

## 使用步骤
1. 工程根目录添加公司maven仓库地址
    ```
    buildscript {
        repositories {
            google()
            jcenter()

        }
        dependencies {
            classpath 'com.android.tools.build:gradle:3.2.1'
            classpath 'me.leayw.tools.build:longan:1.0.0'
        }
    }
    ```

    每个需要独立运行的子模块中添加dependencies
    ```
    dependencies {
            implementation 'me.leayw.longan-api:0.1.6'
        }
    ```

1. 工程根目录下的gradle.properties中田间基本配置
    ```
    mainModuleName=app//正式包模块名称
    versionCode=1//统一版本号，业务需求
    versionName=1.0.0
    ```
2. app模块apply plugin 'com.android.application'替换为'me.leayw.longan', sync
3. sync完成以后会生成debug源码目录，debug是组件单独运行时debug代码的目录，正式打包时这部分代码会被忽略，可以在这里添加单独运行时需要出示话的代码，例如：当前组件的入口activity。正常的java目录下的代码是组件对外提供的代码，业务逻辑应该写到java目录下。
4. 当前组件根目录下会生成gradle.properties文件，文件中包含三个属性:
    1. isDependent属性是判断当前组件是否单独运行；
    2. compileProject中添加依赖的组件，多个组件以逗号分隔；
    3. applicationId是当前独立运行组件的applicationId。
5. 初始化SDK
    ```
    Longan.init(mApplication); // 尽可能早，推荐在Application中初始化
    ```
6. 如果子模块需要初始化操作时，初始化代码写到AppProxy的实现类的onCreate方法。
