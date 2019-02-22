# V14Leanback
[![Wercker](https://img.shields.io/badge/Gradle-2.2.3-brightgreen.svg)]()
[![Wercker](https://img.shields.io/badge/version-V1.1.6-brightgreen.svg)](https://bintray.com/clendy2016/maven/v14leanback/1.1.6)
[![API](https://img.shields.io/badge/API-14%2B-green.svg?style=flat)](https://android-arsenal.com/api?level=14)
[![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)](http://www.apache.org/licenses/LICENSE-2.0)

   <p>   一个适用于Android TV端的分页加载列表库，控件继承自RecyclerView，部分源码抽取自Google Support v17 Leanback包下源码，可兼容低版本环境(API>=14)。
   
Features
--------  
  * 支持分页加载
  * 自动回焦至被选中的item
  * item滚动居中
  * 焦点移动至边界位置时不会出现越界丢失焦点
  * 快速滑动和慢速滑动都不会出现丢焦现象

Screenshots
-------- 

  HorizontalLoadMoreGridView
  
  ![image](https://github.com/Clendy/V14Leanback/blob/master/screenshots/Horizontal.gif)
  
  
  VerticalLoadMoreGridView
  
  ![image](https://github.com/Clendy/V14Leanback/blob/master/screenshots/vertical.gif)
  
Import to your project
--------
You can use Gradle:
```groovy
allprojects {
    repositories {
        jcenter()
    }
}
compile 'io.github.clendy.leanback:v14leanback:1.1.6'
```

Or Maven:
```groovy
<dependency>
  <groupId>io.github.clendy.leanback</groupId>
  <artifactId>v14leanback</artifactId>
  <version>1.1.6</version>
  <type>pom</type>
</dependency>
```

Or Ivy:
```groovy
<dependency org='io.github.clendy.leanback' name='v14leanback' rev='1.1.6'>
  <artifact name='v14leanback' ext='pom' ></artifact>
</dependency>
```
License
--------
```
Copyright (C) 2016 Clendy <yc330483161@163.com | yc330483161@outlook.com>

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
