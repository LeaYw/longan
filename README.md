# FYComponent
组件化开发gradle插件
## 功能介绍：
1. 管理application和library插件
2. 动态依赖组件、支持versions
3. AppProxy（组件的application，执行初始化方法）
4. 资源前缀检查
5. 工程版本号统一管理
6. 组件调试

## 使用步骤

* settings.gradle中添加

    ```
buildscript {
    repositories {
        google()
        jcenter()
        maven {
            credentials {
                username 'nanpRL'
                password 'jEE1TA5jIC'
            }
            url 'https://repo.rdc.aliyun.com/repository/49107-release-3gYVam/'
        }
        maven {
            credentials {
                username 'nanpRL'
                password 'jEE1TA5jIC'
            }
            url 'https://repo.rdc.aliyun.com/repository/49107-snapshot-egC0LB/'
        }
    }
    dependencies {
        classpath 'com.foryou.tools.build:component:latest-version'
    }
}
apply plugin: 'com.foryou.component'

    ```

* 添加公司maven地址
* 添加项目classpath

    ```
    buildscript {
        dependencies {
            classpath 'com.foryou.tools.build:component:latest-version'
        }
    }
    ```
* 如果子模块需要AppProxy，子模块build.gradle中添加dependencies
    ```
    dependencies {
            implementation 'me.leayw.longan-api:0.1.6'
        }
    ```
    初始化代码写到AppProxy的实现类中，具体代码可以参考项目中library模块。

* 组件build.gradle第一行apply plugin 'com.android.application'替换为apply plugin: 'com.foryou.component'

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

* 开启组件调试
    
    ```
    ./gradlew startComponentDebug -P Module=com.foryou.common:coreui -P Branch=dev
    ```
执行成功后会将目标工程（eg:coreui）以及依赖的组件下载到主工程目录下
调试完代码以后，cd到需要提交代码的工程目录下运行git commit、push等操作

* 关闭组件调试
    
    ```
    ./gradlew stopComponentDebug
    ```

