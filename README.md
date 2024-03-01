# Database Commons API

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nervousync/database-api-jdk11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nervousync/database-api-jdk11/)
[![License](https://img.shields.io/github/license/wmkm0113/database-api.svg)](https://github.com/wmkm0113/database-api/blob/master/LICENSE)
![Language](https://img.shields.io/badge/language-Java-green)
[![Twitter:wmkm0113](https://img.shields.io/twitter/follow/wmkm0113?label=Follow)](https://twitter.com/wmkm0113)

English
[简体中文](README_zh_CN.md)
[繁體中文](README_zh_TW.md)

An interface toolkit created for the data processing platform, 
which provides query input and output formats of the unified data platform, 
data import and export tools, lazy loading of data columns or associated data, and other functions. 
It can also automatically encrypt/decrypt and query sensitive data based on annotations. 
Conditional splitting and other operations,

## Table of contents
* [JDK Version](#JDK-Version)
* [End of Life](#End-of-Life)
* [Usage](#Usage)
  + [Add support to the project](#1-add-support-to-the-project)
  + [Add or using the existed implements of the database manager](#2-add-or-using-the-existed-implements-of-the-database-manager)
  + [Initialize the database manager instance](#3-initialize-the-database-manager-instance)
  + [Get the database client instance and operate records](#4get-the-database-client-instance-and-operate-records)
  + [Close the database manager instance](#5close-the-database-manager-instance)
* [Register database table entity class](#register-database-table-entity-class)
* [Sensitive data support](#sensitive-data-support)
* [Data import and export](#data-import-and-export)
  + [Modify the entity class](#modify-the-entity-class)
  + [DataUtils initialize and usage](#datautils-initialize-and-usage)
* [Contributions and feedback](#contributions-and-feedback)
* [Sponsorship and Thanks To](#sponsorship-and-thanks-to)

## JDK Version
Compile：OpenJDK 11   
Runtime: OpenJDK 11+ or compatible version

## End of Life

**Features Freeze:** 31, Dec, 2026   
**Secure Patch:** 31, Dec, 2029

## Usage
### 1. Add support to the project
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

### 2. Add or using the existed implements of the database manager
The toolkit uses Java's SPI to automatically load the database manager implementation class.

**Development the implement class of the database manager:**

1. Create a database manager implementation class. The implementation class must implement the interface org.nervousync.database.api.DatabaseManager. At the same time, the implementation class needs to be annotated with org.nervousync.annotations.provider.Provider.   
2. Create the file META-INF/services/org.nervousync.database.api.DatabaseManager in the project, and write the complete name of the implementation class (package name + class name) in the file.

**Using the existed implements of the database manager:**

Add the repository in the project supports which the repository that contains the database manager implementation class

### 3. Initialize the database manager instance

During the system initialization process, 
call the initialize static method of org.nervousync.database.commons.DatabaseUtils. 
The parameter passed in is the identification code of the database manager implementation class that needs to be used 
the parameter "name" of the annotation org.nervousync.annotations.provider.Provider, 
the toolkit will automatically generate a database manager implementation class object and call the manager's initialize method to perform the initialization operation. 
If an exception occurs during the initialization process, an exception message with the exception code 0x00DB00000004 will be thrown.

### 4.Get the database client instance and operate records
**Obtain the read-only client instance:**   
Obtain the database client using the readOnlyClient static method of org.nervousync.database.commons.DatabaseUtils.
**Obtain the standard client instance:**   
Obtain the database client using the non-parameter static method named retrieveClient of org.nervousync.database.commons.DatabaseUtils.
**Obtain the transactional client instance:**   
1.Obtain the instance by annotations:   
The parameter is the instance class name and method name which method will be using the database client instance.
Toolkit scans the method annotations automatically and generates the transactional client instance 
by the transactional configuring information which generated by the annotations 
if the method was added the annotation org.nervousync.database.annotations.transactional.Transactional.   
2.Obtain the instance by transactional configure instance:   
The parameter is the transactional configure instance (type: org.nervousync.database.beans.configs.transactional.TransactionalConfig),
toolkit will generate the transactional client instance by the given transactional configuring information.   
3.Obtain the instance by identification code:   
The parameter is the identification code of the transactional client instance,
toolkit will find in the registered client instance and return the matched instance.   
**Notice:**   
Toolkit will return null if the annotation not found, 
transactional configure instance is null or identification code not matched when obtaining the transactional client instance.

### 5.Close the database manager instance
The Toolkit will register a hook when the database manager was initialized,
the hook is used to destroy the initialized database manager instance when the system exit(0).

## Register database table entity class
The entity class of database table must extend abstract class org.nervousync.database.entity.core.BeanObject,
and using Jakarta Persistence annotation to identify the table information,
the relation information of tables must add annotations named OneToOne/OneToMany/ManyToOne,
the join columns information using the annotation JoinColumn/JoinColumns.

**Generate value of primary key:**   
If developers want to generate the primary key value automatically,
only need to add the annotation org.nervousync.database.annotations.table.GeneratedValue at the target field,
and configure the parameter "type" of the annotation values:GENERATE/SEQUENCE/ASSIGNED (Default).    
When the value of the "type" parameter is "GENERATE", must configure the parameter named "name" of the annotation, 
the value of the parameter "name" is the generating type of the generated value, toolkit supported the generating type: UUIDv1/UUIDv2/UUIDv3/UUIDv4/UUIDv5/Snowflake/NanoID.
If developers want to add the custom generating type, see org.nervousync.utils.IDUtils.   
When the value of the "type" parameter is "SEQUENCE",
must add the annotation org.nervousync.database.annotations.sequence.SequenceGenerator
to configure the sequence generator.

**Cascade operation annotation:**   
The annotation org.nervousync.database.annotations.table.Options must add to the entity class,
the parameter named "lockOption" will using to lock record at the transactional client, 
the parameter named "dropOption" will using to delete the records when cascade operation.

## Sensitive data support
During the data operation process, you will more or less encounter the processing of sensitive data, 
including but not limited to identification codes, phone numbers, email addresses, bank card numbers,
etc. The toolkit provides simple annotations for sensitive data automatic processing.   
Add the org.nervousync.database.annotations.data.Sensitive annotation on the sensitive data field
that needs to be processed.   
The annotated encField parameter is used to specify the encrypted data storage column, and the parameter secureName is used to specify the security configuration name used for encryption.
The configuration information is as follows:

|      type       |             data type              |     enc result      |
|:---------------:|:----------------------------------:|:-------------------:|
|     NORMAL      | Username/Address/Identify code etc |   w(Hidden info)3   |
| CHN_Social_Code |  Social Credit Code(CHN Mainland)  | 91110(Hidden info)X |
|   CHN_ID_Code   |    Identify Code(CHN Mainland)     |  110(Hidden info)X  |
|     E_MAIL      |           E-Mail address           |   w(Hidden info)m   |
|  PHONE_NUMBER   |            Phone number            |  139(Hidden info)1  |
|      Luhn       |          Bank card number          | 62(Hidden info)8888 |

Please execute the method named "desensitization"
before saving the entity instance to the database, toolkit will authenticate and encrypt the sensitive data content.
Notice that if the parameter "type" values are NOT "NORMAL"
toolkit will ignore the encrypting if the sensitive data content not matched the rule of the data type.

## Data import and export
Toolkit will convert between the Excel file and entity instance automatically
if developers add some annotations to the entity class.
### Modify the entity class
**1.Add Sheet annotation:**   
Add annotation org.nervousync.database.annotations.data.ExcelSheet to the entity class,
the parameter "value" is the sheet name of the entity class.   
**2.Add data column annotation:**   
Add annotation org.nervousync.database.annotations.data.ExcelColumn to the column field,
parameter "value" is the index value of the Excel column to the current field. 

### DataUtils initialize and usage
**1.DataUtils initialize:**   
Call the initialize static method of org.nervousync.database.commons.DataUtils to initialize the data utilities. 
The parameter named "basePath" is the default work folder of the data utilities,
all import/export temporary files will be saved to the default work folder.
+ Parameter "providerName" is the identification code of the task provider implements class, this provider will use to save the task information, if the identification code is empty string or not found, the task information will save it to memory, and the task information will be lost when the utilities were shutdown.
+ Parameter "threadLimit" is the thread limit count of the running threads.
+ Parameter "expireTime" is the expiry time of the finished task clear.

**2.Add task to import data:**   
Call the addTask method of org.nervousync.database.commons.DataUtils to add a data import task.
+ Parameter "inputStream" is the input stream instance of the Excel file which file will import into the database.
+ Parameter "userCode" is the identification code of the operator, operators only can query the task information matched the same identification code.
+ Parameter "transactional" is the flag value of using transactional to import the data, parameter value type: boolean.
+ Parameter "timeout" is the time-out value of transactional.

**3.Add task to export data:**   
Call the addTask method of org.nervousync.database.commons.DataUtils to add a data export task.
+ Parameter "userCode" is the identification code of the operator, operators only can query the task information matched the same identification code.
+ Parameter "queryInfos" is the query information arrays of the export data, one task can export multiple data tables to the same Excel file.

**4.Update configure information:**   
Call the config method of org.nervousync.database.commons.DataUtils to update configure information.
+ Parameter "threadLimit" is the thread limit count of the running threads.
+ Parameter "expireTime" is the expiry time of the finished task clear.

## Contributions and feedback
Friends are welcome to translate the prompt information, error messages, 
etc. in this document and project into more languages to help more users better understand and use this toolkit.   
If you find problems during use or need to improve or add related functions, please submit an issue to this project
or send email to [wmkm0113\@gmail.com](mailto:wmkm0113@gmail.com?subject=bugs_and_features)   
For better communication, please include the following information when submitting an issue or sending an email:
1. The purpose is: discover bugs/function improvements/add new features   
2. Please paste the following information (if it exists): incoming data, expected results, error stack information   
3. Where do you think there may be a problem with the code (if provided, it can help us find and solve the problem as soon as possible)

If you are submitting information about adding new features, please ensure that the features to be added are general needs, that is, the new features can help most users.

If you need to add customized special requirements, I will charge a certain custom development fee.
The specific fee amount will be assessed based on the workload of the customized special requirements.   
For customized special features, please send an email directly to [wmkm0113\@gmail.com](mailto:wmkm0113@gmail.com?subject=payment_features). At the same time, please try to indicate the budget amount of development cost you can afford in the email.

## Sponsorship and Thanks To
<span id="JetBrains">
    <img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png" width="100px" height="100px" alt="JetBrains Logo (Main) logo.">
    <span>Many thanks to <a href="https://www.jetbrains.com/">JetBrains</a> for sponsoring our Open Source projects with a license.</span>
</span>