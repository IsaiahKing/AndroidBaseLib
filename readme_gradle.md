# Gradle 配置文件 文档说明

```groovy

apply plugin: 'com.android.application'

android {

    signingConfigs {
        //        正式发布使用签名文件 名字自定义
        app_key {
            keyAlias 'app-key'
            keyPassword '123456'
            storeFile file('../app_key.jks')
            storePassword '123456'
        }
        bate_app_key {
            keyAlias 'app-key'
            keyPassword '123456'
            storeFile file('../app_key.jks')
            storePassword '123456'
        }
        //        debug调试编译发布使用签名文件 名字自定义
        debug_app_key {
            keyAlias 'app-key'
            keyPassword '123456'
            storeFile file('../app_key.jks')
            storePassword '123456'
        }
    }

    //设置编译时使用的构建工具的版本，Android Studio3.0后去除此项配置
    compileSdkVersion 28
    //设置编译时使用的构建工具的版本，Android Studio3.0后去除此项配置
    //buildToolsVersion：
    defaultConfig {
        //项目的包名
        applicationId "com.studyyoun.husband"
        //项目最低兼容的版本
        minSdkVersion 19
        //项目的目标版本
        targetSdkVersion 28
        //版本号
        versionCode 19111234
        ////版本名称
        versionName "2.0.0"
        testInstrumentationRunner "android.support.test.runner.AndroidJUnitRunner"
        externalNativeBuild {
            cmake {
                cppFlags ""
            }
        }
        //新的 gradle 插件不再支持 annotation processors，如果需要使用需要显式声明
        javaCompileOptions {
            // 显式声明支持注解
            annotationProcessorOptions {
                includeCompileClasspath true
            }
        }
        manifestPlaceholders = [
                JPUSH_PKGNAME: applicationId,
                JPUSH_APPKEY : "0c643564dac4b41a1e529a03", //JPush 上注册的包名对应的 Appkey.
                JPUSH_CHANNEL: "developer-default", //暂时填写默认值即可.
        ]
    }
    applicationVariants.all { variant ->
        variant.outputs.all { output ->
            def outputFile = output.outputFile
            def fileName
            if (outputFile != null && outputFile.name.endsWith('.apk')) {
                if (variant.buildType.name.equals('release')) {//如果是release包
                    fileName = "vote_release_v${defaultConfig.versionCode}.apk"
                } else if (variant.buildType.name.equals('debug')) {//如果是debug包
                    fileName = "vote_debug_v${defaultConfig.versionCode}.apk"
                } else if (variant.buildType.name.equals('beta')) {//如果是beta包
                    fileName = "vote_beta_v${defaultConfig.versionCode}.apk"
                }
                outputFileName = fileName
            }
        }
    }
    packagingOptions{
        //pickFirsts做用是 当有重复文件时 打包会报错 这样配置会使用第一个匹配的文件打包进入apk
        // 表示当apk中有重复的META-INF目录下有重复的LICENSE文件时  只用第一个 这样打包就不会报错
        pickFirsts = ['META-INF/LICENSE']

        //merges何必 当出现重复文件时 合并重复的文件 然后打包入apk
        //这个是有默认值得 merges = [] 这样会把默默认值去掉  所以我们用下面这种方式 在默认值后添加
        merge 'META-INF/LICENSE'

        //这个是在同时使用butterknife、dagger2做的一个处理。同理，遇到类似的问题，只要根据gradle的提示，做类似处理即可。
        exclude 'META-INF/services/javax.annotation.processing.Processor'
    }
    buildTypes {
        debug {
            //设置签名信息
            signingConfig signingConfigs.debug_app_key
            ////是否对代码进行混淆
            minifyEnabled false
            //指定混淆的规则文件
            proguardFiles getDefaultProguardFile('proguard-android.txt'), 'proguard-rules.pro'
            //是否开启LOG日志功能
            buildConfigField('boolean', "API_LOG", "true")
            // 配置URL前缀
            buildConfigField 'String', 'SERVER_URL', '"http://192.168.0.100:8080/headline/app/api/"'
            //是否在APK中生成伪语言环境，帮助国际化的东西，一般使用的不多
            pseudoLocalesEnabled false
            //是否对APK包执行ZIP对齐优化，减小zip体积，增加运行效率
            zipAlignEnabled true
            //在applicationId 中添加了一个后缀，一般使用的不多
            applicationIdSuffix 'test'
            //在applicationId 中添加了一个后缀，一般使用的不多
            versionNameSuffix 'test'
            //是否支持断点调试
            debuggable true
            //是否可以调试NDK代码
            jniDebuggable true
            //是否开启渲染脚本就是一些c写的渲染方法
            renderscriptDebuggable false
        }
        beta {
            signingConfig signingConfigs.bate_app_key
            buildConfigField('boolean', "API_LOG", "false")
            //是否对代码进行混淆
            minifyEnabled false
            buildConfigField 'String', 'SERVER_URL', '"http://www.studyyoun.com/headline/app/api/"'
        }
        release {
            signingConfig signingConfigs.app_key
            //是否开启LOG日志功能
            buildConfigField('boolean', "API_LOG", "false")
            //是否对代码进行混淆
            minifyEnabled false
            buildConfigField 'String', 'SERVER_URL', '"http://www.studyyoun.com/headline/app/api/"'
        }
    }
    //程序在编译的时候会检查lint，有任何错误提示会停止build，我们可以关闭这个开关
    lintOptions {
        //即使报错也不会停止打包
        checkReleaseBuilds false
        //打包release版本的时候进行检测
        abortOnError false
    }

    //目录指向配置
    sourceSets {

        main {
            //指定lib库目录
            //可以在Android studio的Android视图下生成jniLibs文件夹，可以方便我们存放jar包和库文件
            jniLibs.srcDirs = ['libs']
        }
    }
}
/**
 * 从Android Studio3.0后compile引入库不在使用，而是通过api和implementation，
 * api完全等同于以前的compile，用api引入的库整个项目都可以使用，用implementation引入的库只有对应的Module能使用，
 * 其他Module不能使用，由于之前的项目统一用compile依赖，导致的情况就是模块耦合性太高，不利于项目拆解，使用implementation之后虽然使用起来复杂了但是做到降低偶合兴提高安全性
 *
 */
dependencies {
    implementation fileTree(include: ['*.jar'], dir: 'libs')
    api fileTree(include: ['*.aar'], dir: 'libs')
    implementation 'com.android.support:appcompat-v7:28.0.0'
    implementation 'com.android.support.constraint:constraint-layout:1.1.3'
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'com.android.support.test:runner:1.0.2'
    androidTestImplementation 'com.android.support.test.espresso:espresso-core:3.0.2'
    implementation 'com.android.support:recyclerview-v7:28.0.0'
    implementation project(':cameralibrary')
    implementation project(':scanlistlibrary')
    implementation project(':zxinglite')
}


```

