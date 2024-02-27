# 数据库通用接口

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nervousync/database-api-jdk11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nervousync/database-api-jdk11/)
[![License](https://img.shields.io/github/license/wmkm0113/database-api.svg)](https://github.com/wmkm0113/database-api/blob/master/LICENSE)
![Language](https://img.shields.io/badge/language-Java-green)
[![Twitter:wmkm0113](https://img.shields.io/twitter/follow/wmkm0113?label=Follow)](https://twitter.com/wmkm0113)

[English](README.md)
简体中文

为数据处理平台打造的接口工具包，提供统一数据平台的查询输入输出格式、数据导入导出工具、懒加载数据列或关联数据等功能，同时还可以根据注解自动对敏感数据进行加密/解密、查询条件拆分等操作，

## 目录
* [JDK版本](#JDK版本)
* [生命周期](#生命周期)
* [使用方法](#使用方法)
  + [在项目中添加支持](#1在项目中添加支持)
  + [添加或引用数据库管理器](#2添加或引用数据库管理器)
  + [初始化数据库管理器](#3初始化数据库管理器)
  + [获取数据库客户端并执行相关操作](#4获取数据库客户端并执行相关操作)
  + [关闭数据库管理器](#5关闭数据库管理器)
* [注册数据表实体类](#注册数据表实体类)
* [敏感数据的处理](#敏感数据的处理)
* [数据导入导出](#数据导入导出)
  + [数据表实体类的修改](#数据表实体类的修改)
  + [导入导出工具的初始化和使用](#导入导出工具的初始化和使用)
* [贡献与反馈](#贡献与反馈)
* [赞助与鸣谢](#赞助与鸣谢)

## JDK版本：
编译：OpenJDK 11   
运行：OpenJDK 11+ 或兼容版本

## 生命周期：
**功能冻结：** 2026年12月31日   
**安全更新：** 2029年12月31日

## 使用方法：
### 1、在项目中添加支持
**Maven:**   
```
<dependency>
    <groupId>org.nervousync</groupId>
	<artifactId>database-api-jdk11</artifactId>
    <version>${version}</version>
</dependency>
```
**Gradle:**   
```
Manual: compileOnly group: 'org.nervousync', name: 'database-api-jdk11', version: '${version}'
Short: compileOnly 'org.nervousync:database-api-jdk11:${version}'
```
**SBT:**   
```
libraryDependencies += "org.nervousync" % "database-api-jdk11" % "${version}" % "provided"
```
**Ivy:**   
```
<dependency org="org.nervousync" name="database-api-jdk11" rev="${version}"/>
```

### 2、添加或引用数据库管理器
工具包使用Java的SPI对数据库管理器实现类进行自动加载。

**编写数据库管理器：**

1、创建数据库管理器实现类，实现类必须实现接口 org.nervousync.database.api.DatabaseManager，同时实现类需要使用 org.nervousync.annotations.provider.Provider 进行注解。   
2、在项目中创建文件 META-INF/services/org.nervousync.database.api.DatabaseManager，并在文件中写明实现类的完整名称（包名+类名）。

**引用已有的数据库管理器：**

在项目中引用包含数据库管理器实现类的项目支持

### 3、初始化数据库管理器
在系统初始化的过程中调用 org.nervousync.database.commons.DatabaseUtils 的 initialize 静态方法，传入的参数为需要使用的数据库管理器实现类的识别代码（org.nervousync.annotations.provider.Provider 注解的 name 参数），工具包会自动生成数据库管理器实现类对象并调用管理器的 initialize 方法执行初始化操作，如果在初始化的过程中出现异常，则会抛出异常代码为0x00DB00000005的异常信息。

### 4、获取数据库客户端并执行相关操作
**获取只读模式的数据库客户端：**   
使用 org.nervousync.database.commons.DatabaseUtils 的 readOnlyClient 静态方法获取数据库客户端。   
**获取常规模式的数据库客户端：**   
使用 org.nervousync.database.commons.DatabaseUtils 无参数的 retrieveClient 静态方法获取数据库客户端。   
**获取事务模式的数据库客户端：**   
使用 org.nervousync.database.commons.DatabaseUtils 包含参数的 retrieveClient 静态方法获取数据库客户端。   
1、根据注解获取事务模式的数据库客户端：   
参数包括使用数据库客户端的类名和方法名，工具包会自动扫描对应的方法信息，如果方法添加了 org.nervousync.database.annotations.transactional.Transactional 注解，则根据注解的配置生成对应事务模式的数据库客户端。   
2、显示传递事务配置信息获取事务模式的数据库客户端：   
参数为事务配置信息实例对象，工具包生成对应配置信息的事务模式数据库客户端。   
3、根据事务识别代码获取事务模式的数据库客户端：   
参数为事务识别代码，工具包会在记录的数据库客户端中进行查找，并返回对应的务模式数据库客户端。   
**注意：**
获取事务模式的数据库客户端时，如果未找到配置信息或给定事务识别代码对应的数据库客户端，则返回 null。 带有事务支持的客户端需要显式调用 rollbackTransactional/endTransactional 完成事务的回滚或提交。

### 5、关闭数据库管理器
在数据库管理器初始化的过程中，工具包会自动注册数据库管理器的关闭操作，当系统正常终止时，工具包会自动调用 org.nervousync.database.commons.DatabaseUtils 的 destroy 静态方法，执行回滚并关闭所有数据库客户端，停止数据导入导出操作。

## 注册数据表实体类
数据表实体类需要继承 org.nervousync.database.entity.core.BeanObject类，并且需要使用Jakarta Persistence进行注解，数据表的关联信息需要使用OneToOne/OneToMany/ManyToOne进行注解，并使用JoinColumn/JoinColumns标注关联列信息。   

**主键值生成：**    
如果需要工具包自动生成主键，需要在对应的属性上添加注解 org.nervousync.database.annotations.table.GeneratedValue，参数 type 可以为 自动生成（GENERATE）/序列（SEQUENCE）/显示设置（ASSIGNED），默认值为：显示设置（ASSIGNED）。   
当 type 值为“自动生成（GENERATE）”时，需要设置 name 参数用于键值的生成，参数值为键值的生成方式，工具包中支持“s”。
当 type 值为“序列（SEQUENCE）”时，需要同时添加 org.nervousync.database.annotations.sequence.SequenceGenerator 注解，用于设置序列生成器的相关配置信息。

**关联操作注解：**    
注解 org.nervousync.database.annotations.table.Options 需要标注在数据表实体类上，其中的参数 lockOption 用于配置事务中数据锁定模式。参数 dropOption 用于级联删除时的操作模式。

## 敏感数据的处理
在数据操作过程中，或多或少都会遇到敏感数据的处理，包括但不限于身份识别代码、电话号码、电子邮箱地址、银行卡号等，工具包中提供了简单的注解用于对敏感数据的自动处理。   
在需要处理的敏感数据属性上添加 org.nervousync.database.annotations.data.Sensitive 注解。   
注解的 encField 参数用于指定加密后的数据存储列，参数 secureName 用于指定加密使用的安全配置名称。   
配置信息如下：

|     type参数      |     数据类型     |    加密结果样例    |
|:---------------:|:------------:|:------------:|
|     NORMAL      |  用户名、地址信息等   |   w（隐藏信息）3   |
| CHN_Social_Code | 中国大陆统一信用识别代码 | 91110（隐藏信息）X |
|   CHN_ID_Code   |  中国大陆身份证号码   |  110（隐藏信息）X  |
|     E_MAIL      |    电子邮件地址    |   w（隐藏信息）m   |
|  PHONE_NUMBER   |     电话号码     |  139（隐藏信息）1  |
|      Luhn       |    银行卡号码     | 62（隐藏信息）8888 |

在保存包含敏感信息的数据表实体类对象时，需要显示调用 desensitization 方法，让工具包对敏感数据进行验证和加密处理。需要注意的是，如果您的敏感数据值和配置的type参数类型不匹配，工具包会忽略对这些不匹配的敏感数据的处理。

## 数据导入导出
在数据表实体类中添加配置注解，可以让工具包自动添加数据表和Excel文件的相互转化工具。
### 数据表实体类的修改
**1、添加工作表注解：**   
在数据表实体类上添加 org.nervousync.database.annotations.data.ExcelSheet 注解，参数 value 为工作表的名称。   
**2、添加数据列注解：**   
在数据列属性上添加 org.nervousync.database.annotations.data.ExcelColumn 注解。参数 value 为对应Excel数据列的索引值，起始值为：0

### 导入导出工具的初始化和使用
**1、工具的初始化：**   
显示调用 org.nervousync.database.commons.DataUtils 的 initialize 静态方法，传入对应的参数配置信息，初始化导入导出工具。   
其中 basePath 参数为导入导出工具的默认工作目录，所有导入导出文件会临时存储到此目录中。   
+ 参数 providerName 为任务存储适配器的识别代码，用于保存导入导出任务，如果未指定存储适配器，则所有任务信息存储在内存中，当工具停止工作后会丢失未完成的任务信息。   
+ 参数 threadLimit 为允许同时执行的任务线程数。   
+ 参数 expireTime 为已完成的任务在此时间以后，会被删除掉任务信息。

**2、数据导入任务的添加：**   
调用 org.nervousync.database.commons.DataUtils 的 addTask 方法添加数据导入任务。   
+ 参数 inputStream 为需要导入的Excel文件输入流实例对象。   
+ 参数 userCode 为添加任务的操作员识别代码，每个操作员仅可以查询到自己的任务信息。   
+ 参数 transactional 为是否使用事务模式进行数据导入的状态值，数据类型为：boolean。   
+ 参数 timeout 为事务模式下的超时时间。

**3、数据导出任务的添加：**   
调用 org.nervousync.database.commons.DataUtils 的 addTask 方法添加数据导出任务。   
+ 参数 userCode 为添加任务的操作员识别代码，每个操作员仅可以查询到自己的任务信息。   
+ 参数 queryInfos 为导出数据的查询信息数组，可以在一个导出任务中导出多个数据表的数据，并将这些数据保存在同一个Excel文件中。   

**4、工具的配置信息更新：**   
调用 org.nervousync.database.commons.DataUtils 的 config 方法更新配置信息。   
+ 参数 threadLimit 为允许同时执行的任务线程数。   
+ 参数 expireTime 为已完成的任务在此时间以后，会被删除掉任务信息。

## 贡献与反馈
欢迎各位朋友将此文档及项目中的提示信息、错误信息等翻译为更多语言，以帮助更多的使用者更好地了解与使用此工具包。   
如果在使用过程中发现问题或需要改进、添加相关功能，请提交issue到本项目或发送电子邮件到[wmkm0113\@gmail.com](mailto:wmkm0113@gmail.com?subject=bugs_and_features)   
为了更好地沟通，请在提交issue或发送电子邮件时，写明如下信息：   
1、目的是：发现Bug/功能改进/添加新功能   
2、请粘贴以下信息（如果存在）：传入数据，预期结果，错误堆栈信息   
3、您认为可能是哪里的代码出现问题（如提供可以帮助我们尽快地找到并解决问题）   
如果您提交的是添加新功能的相关信息，请确保需要添加的功能是一般性的通用需求，即添加的新功能可以帮助到大多数使用者。

如果您需要添加的是定制化的特殊需求，我将收取一定的定制开发费用，具体费用金额根据定制化的特殊需求的工作量进行评估。   
定制化特殊需求请直接发送电子邮件到[wmkm0113\@gmail.com](mailto:wmkm0113@gmail.com?subject=payment_features)，同时请尽量在邮件中写明您可以负担的开发费用预算金额。

## 赞助与鸣谢
<span id="JetBrains">
    <img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png" width="100px" height="100px" alt="JetBrains Logo (Main) logo.">
    <span>非常感谢 <a href="https://www.jetbrains.com/">JetBrains</a> 通过许可证赞助我们的开源项目。</span>
</span>