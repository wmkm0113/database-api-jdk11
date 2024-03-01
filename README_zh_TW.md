# 資料庫通用介面

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.nervousync/database-api-jdk11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.nervousync/database-api-jdk11/)
[![License](https://img.shields.io/github/license/wmkm0113/database-api-jdk11.svg)](https://github.com/wmkm0113/database-api-jdk11/blob/master/LICENSE)
![Language](https://img.shields.io/badge/language-Java-green)
[![Twitter:wmkm0113](https://img.shields.io/twitter/follow/wmkm0113?label=Follow)](https://twitter.com/wmkm0113)

[English](README.md)
[简体中文](README_zh_CN.md)
繁體中文

為資料處理平臺打造的介面工具包，提供統一資料平臺的查詢輸入輸出格式、資料導入匯出工具、懶載入資料列或關聯資料等功能，同時還可以根據注解自動對敏感性資料進行加密/解密、查詢準則拆分等操作，

## 目錄
* [JDK版本](#JDK版本)
* [生命週期](#生命週期)
* [使用方法](#使用方法)
  + [在專案中添加支持](#1在專案中添加支持)
  + [添加或引用資料庫管理器](#2添加或引用資料庫管理器)
  + [初始化資料庫管理器](#3初始化資料庫管理器)
  + [獲取資料庫用戶端並執行相關操作](#4獲取資料庫用戶端並執行相關操作)
  + [關閉資料庫管理器](#5關閉資料庫管理器)
* [註冊資料表實體類](#註冊資料表實體類)
* [敏感性資料的處理](#敏感性資料的處理)
* [資料導入匯出](#數據導入匯出)
  + [資料表實體類的修改](#資料表實體類的修改)
  + [導入匯出工具的初始化和使用](#導入匯出工具的初始化和使用)
* [貢獻與回饋](#貢獻與回饋)
* [贊助與鳴謝](#贊助與鳴謝)

## JDK版本：
編譯：OpenJDK 11   
運行：OpenJDK 11+ 或相容版本

## 生命週期：
**功能凍結：** 2026年12月31日   
**安全更新：** 2029年12月31日

## 使用方法：
### 1、在專案中添加支持
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

### 2、添加或引用資料庫管理器
工具包使用Java的SPI對資料庫管理器實現類進行自動載入。

**編寫資料庫管理器：**

1、創建資料庫管理器實現類，實現類必須實現介面 org.nervousync.database.api.DatabaseManager，同時實現類需要使用 org.nervousync.annotations.provider.Provider 進行注解。   
2、在專案中創建檔 META-INF/services/org.nervousync.database.api.DatabaseManager，並在檔中寫明實現類的完整名稱（包名+類名）。

**引用已有的資料庫管理器：**

在專案中引用包含資料庫管理器實現類的專案支持

### 3、初始化資料庫管理器
在系統初始化的過程中調用 org.nervousync.database.commons.DatabaseUtils 的 initialize 靜態方法，傳入的參數為需要使用的資料庫管理器實現類的識別代碼（org.nervousync.annotations.provider.Provider 注解的 name 參數），工具包會自動生成資料庫管理器實現類物件並調用管理器的 initialize 方法執行初始化操作，如果在初始化的過程中出現異常，則會拋出異常代碼為0x00DB00000004的異常資訊。

### 4、獲取資料庫用戶端並執行相關操作
**獲取唯讀模式的資料庫用戶端：**   
使用 org.nervousync.database.commons.DatabaseUtils 的 readOnlyClient 靜態方法獲取資料庫用戶端。   
**獲取常規模式的資料庫用戶端：**   
使用 org.nervousync.database.commons.DatabaseUtils 無參數的 retrieveClient 靜態方法獲取資料庫用戶端。   
**獲取事務模式的資料庫用戶端：**   
使用 org.nervousync.database.commons.DatabaseUtils 包含參數的 retrieveClient 靜態方法獲取資料庫用戶端。   
1、根據注解獲取事務模式的資料庫用戶端：   
參數包括使用資料庫用戶端的類名和方法名，工具包會自動掃描對應的方法資訊，如果方法添加了 org.nervousync.database.annotations.transactional.Transactional 注解，則根據注解的配置生成對應事務模式的資料庫用戶端。   
2、顯示傳遞事務配置資訊獲取事務模式的資料庫用戶端：   
參數為事務配置資訊實例物件，工具包生成對應配置資訊的事務模式資料庫用戶端。   
3、根據事務識別代碼獲取事務模式的資料庫用戶端：   
參數為事務識別代碼，工具包會在記錄的資料庫用戶端中進行查找，並返回對應的務模式資料庫用戶端。   
**注意：**
獲取事務模式的資料庫用戶端時，如果未找到配置資訊或給定事務識別代碼對應的資料庫用戶端，則返回 null。 帶有事務支援的用戶端需要顯式調用 rollbackTransactional/endTransactional 完成事務的回滾或提交。

### 5、關閉資料庫管理器
在資料庫管理器初始化的過程中，工具包會自動註冊資料庫管理器的關閉操作，當系統正常終止時，工具包會自動調用 org.nervousync.database.commons.DatabaseUtils 的 destroy 靜態方法，執行回滾並關閉所有資料庫用戶端，停止資料導入匯出操作。

## 註冊資料表實體類
資料表實體類需要繼承 org.nervousync.database.entity.core.BeanObject類，並且需要使用Jakarta Persistence進行注解，資料表的關聯資訊需要使用OneToOne/OneToMany/ManyToOne進行注解，並使用JoinColumn/JoinColumns標注關聯列資訊。   

**主鍵值生成：**    
如果需要工具包自動生成主鍵，需要在對應的屬性上添加注解 org.nervousync.database.annotations.table.GeneratedValue，參數 type 可以為 自動生成（GENERATE）/序列（SEQUENCE）/顯示設定（ASSIGNED），預設值為：顯示設定（ASSIGNED）。   
當 type 值為“自動生成（GENERATE）”時，需要設置 name 參數用於鍵值的生成，參數值為鍵值的生成方式，工具包中支援“s”。
當 type 值為“序列（SEQUENCE）”時，需要同時添加 org.nervousync.database.annotations.sequence.SequenceGenerator 注解，用於設置序列生成器的相關配置資訊。

**關聯操作注解：**    
注解 org.nervousync.database.annotations.table.Options 需要標注在資料表實體類上，其中的參數 lockOption 用於配置事務中資料鎖定模式。參數 dropOption 用於級聯刪除時的操作模式。

## 敏感性資料的處理
在資料操作過程中，或多或少都會遇到敏感性資料的處理，包括但不限於身份識別代碼、電話號碼、電子郵箱位址、銀行卡號等，工具包中提供了簡單的注解用於對敏感性資料的自動處理。   
在需要處理的敏感性資料屬性上添加 org.nervousync.database.annotations.data.Sensitive 注解。   
注解的 encField 參數用於指定加密後的資料存儲列，參數 secureName 用於指定加密使用的安全配置名稱。   
配置資訊如下：

|     type參數      |     資料類型     |    加密結果樣例    |
|:---------------:|:------------:|:------------:|
|     NORMAL      |  用戶名、位址資訊等   |   w（隱藏資訊）3   |
| CHN_Social_Code | 中國大陸統一信用識別代碼 | 91110（隱藏資訊）X |
|   CHN_ID_Code   |  中國大陸身份證號碼   |  110（隱藏資訊）X  |
|     E_MAIL      |    電子郵寄地址    |   w（隱藏資訊）m   |
|  PHONE_NUMBER   |     電話號碼     |  139（隱藏資訊）1  |
|      Luhn       |    銀行卡號碼     | 62（隱藏資訊）8888 |

在保存包含敏感資訊的資料表實體類物件時，需要顯示調用 desensitization 方法，讓工具包對敏感性資料進行驗證和加密處理。需要注意的是，如果您的敏感性資料值和配置的type參數類型不匹配，工具包會忽略對這些不匹配的敏感性資料的處理。

## 數據導入匯出
在資料表實體類中添加配置注解，可以讓工具包自動添加資料表和Excel檔的相互轉化工具。
### 資料表實體類的修改
**1、添加工作表注解：**   
在資料表實體類上添加 org.nervousync.database.annotations.data.ExcelSheet 注解，參數 value 為工作表的名稱。   
**2、添加數據列注解：**   
在資料列屬性上添加 org.nervousync.database.annotations.data.ExcelColumn 注解。參數 value 為對應Excel資料列的索引值，起始值為：0

### 導入匯出工具的初始化和使用
**1、工具的初始化：**   
顯示調用 org.nervousync.database.commons.DataUtils 的 initialize 靜態方法，傳入對應的參數配置資訊，初始化導入匯出工具。   
其中 basePath 參數為導入匯出工具的預設工作目錄，所有導入匯出檔會臨時存儲到此目錄中。   
+ 參數 providerName 為任務存儲適配器的識別代碼，用於保存導入匯出任務，如果未指定存儲適配器，則所有任務資訊存儲在記憶體中，當工具停止工作後會丟失未完成的任務資訊。   
+ 參數 threadLimit 為允許同時執行的任務執行緒數。   
+ 參數 expireTime 為已完成的任務在此時間以後，會被刪除掉任務資訊。

**2、資料導入任務的添加：**   
調用 org.nervousync.database.commons.DataUtils 的 addTask 方法添加資料導入任務。   
+ 參數 inputStream 為需要導入的Excel檔輸入流實例物件。   
+ 參數 userCode 為添加任務的操作員識別代碼，每個操作員僅可以查詢到自己的任務資訊。   
+ 參數 transactional 為是否使用事務模式進行資料導入的狀態值，資料類型為：boolean。   
+ 參數 timeout 為事務模式下的超時時間。

**3、資料匯出任務的添加：**   
調用 org.nervousync.database.commons.DataUtils 的 addTask 方法添加資料匯出任務。   
+ 參數 userCode 為添加任務的操作員識別代碼，每個操作員僅可以查詢到自己的任務資訊。   
+ 參數 queryInfos 為匯出資料的查詢資訊陣列，可以在一個匯出任務中匯出多個資料表的資料，並將這些資料保存在同一個Excel檔中。   

**4、工具的配置資訊更新：**   
調用 org.nervousync.database.commons.DataUtils 的 config 方法更新配置資訊。   
+ 參數 threadLimit 為允許同時執行的任務執行緒數。   
+ 參數 expireTime 為已完成的任務在此時間以後，會被刪除掉任務資訊。

## 貢獻與回饋
歡迎各位朋友將此文檔及專案中的提示資訊、錯誤資訊等翻譯為更多語言，以説明更多的使用者更好地瞭解與使用此工具包。   
如果在使用過程中發現問題或需要改進、添加相關功能，請提交issue到本專案或發送電子郵件到[wmkm0113\@gmail.com](mailto:wmkm0113@gmail.com?subject=bugs_and_features)   
為了更好地溝通，請在提交issue或發送電子郵件時，寫明如下資訊：   
1、目的是：發現Bug/功能改進/添加新功能   
2、請粘貼以下資訊（如果存在）：傳入資料，預期結果，錯誤堆疊資訊   
3、您認為可能是哪裡的代碼出現問題（如提供可以幫助我們儘快地找到並解決問題）   
如果您提交的是添加新功能的相關資訊，請確保需要添加的功能是一般性的通用需求，即添加的新功能可以幫助到大多數使用者。

如果您需要添加的是定制化的特殊需求，我將收取一定的定制開發費用，具體費用金額根據定制化的特殊需求的工作量進行評估。   
定制化特殊需求請直接發送電子郵件到[wmkm0113\@gmail.com](mailto:wmkm0113@gmail.com?subject=payment_features)，同時請儘量在郵件中寫明您可以負擔的開發費用預算金額。

## 贊助與鳴謝
<span id="JetBrains">
    <img src="https://resources.jetbrains.com/storage/products/company/brand/logos/jb_beam.png" width="100px" height="100px" alt="JetBrains Logo (Main) logo.">
    <span>非常感謝 <a href="https://www.jetbrains.com/">JetBrains</a> 通過許可證贊助我們的開源項目。</span>
</span>

