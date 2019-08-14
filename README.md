# Oreo
组件化开发gradle插件
## 功能介绍：
1. 管理application和library插件
2. 动态依赖组件、支持versions
3. AppProxy
4. 资源前缀检查
5. 工程版本号统一管理

## 使用步骤
* 添加公司maven地址
* 添加项目classpath

    ```
    buildscript {
        dependencies {
            classpath 'me.leayw.tools.build:longan:1.1.0'
        }
    }
    ```
* 如果子模块需要AppProxy，子模块build.gradle中添加dependencies
    ```
    dependencies {
            implementation 'me.leayw.longan-api:0.1.6'
        }
    ```

* 组件build.gradle第一行apply plugin 'com.android.application'替换为'me.leayw.longan'

* 添加动态依赖
    
    ```
    longan{
        dynamicDependencies {//使用方式同dependencies
            implementation qrcode
        }
    }
    ```

* 初始化SDK
    ```
    Longan.init(mApplication); // 尽可能早，推荐在Application中初始化
    ```
* 如果子模块需要初始化操作时，初始化代码写到AppProxy的实现类的onCreate方法。
