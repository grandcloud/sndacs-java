#盛大云存储服务Java SDK

[盛大云存储服务](http://www.grandcloud.cn/product/ecs)的官方Java SDK。

1. DSL(Fluent Interface)风格的API，简洁易用
2. 支持Access Policy Language，通过DSL风格的API生成Bucket Policy
3. 支持大文件上传（最大5TB），对于大文件则自动通过Multipart Upload实现上传，对开发者透明
4. 提供了独立的签名与认证模块供开发者使用
5. 支持HTTPS安全网络访问
6. 无需配置Endpoint，自动支持多盛大云存储服务的多IDC
7. 支持限速传输

## 下载

1. [snda-cloud-storage-java-sdk-2.0.0-SNAPSHOT.jar](http://www.grandcloud.cn/product/ecs)	二进制发布包
2. [snda-cloud-storage-java-sdk-2.0.0-SNAPSHOT.zip](http://www.grandcloud.cn/product/ecs)	包含源代码，第三方依赖，Javadoc等内容的发布包

## Maven依赖
```xml
    <dependency>
      <groupId>com.snda</groupId>
      <artifactId>snda-cloud-storage-java-sdk</artifactId>
      <version>2.0.0</version>
    </dependency>
```
## 使用
盛大云存储服务Java SDK提供了DSL风格的API，简单易用，核心为SNDAStorage对象，开发者可通过该对象提供的各种方法来访问盛大云存储服务。

### 构建SNDAStorage对象

```java
	SNDAStorage storage = new SNDAStorageBuilder().credential(yourAccessKeyId, yourSecretAccessKey).build();
```
更多的设置：
```java
	SNDAStorage storage = new SNDAStorageBuilder().
		credential(yourAccessKeyId, yourSecretAccessKey).
		https(). 						//启用HTTPS
		bytesPerSecond(64 * 1024).		//限制每秒传输速率为64KB
		connectionTimeout(10 * 1000).	//设置ConnectionTimeout为10秒
		soTimeout(30 * 1000).			//设置SoTimeout为30秒
		build();
```		


SNDAStorage对象内部维护了一组HTTP连接池，在不使用该对象时，应该调用其destory方法销毁该对象，
```java
	storage.destory();
```
	
***最佳实践***：一般可在Spring容器退出时，Servlet容器退出时，调用storage对象的destory方法

### Bucket相关的操作
```java
    storage.bucket("mybucket").create();			//在默认的Location节点中创建名为mybucket的Bucket
    
    storage.bucket("mybucket").location(Location.HUADONG_1).create();	//在华东一节点中创建名为mybucket的Bucket
    
    storage.bucket("mybucket").location().get();	//查看该Bucket的Location

	storage.bucket("mybucket").delete();			//删除该Bucket
```

### 上传与下载Object

将C:\\My Picture.jpg文件上传至mybucket中，盛大云存储SDK会自动设置该Object的ContentType与ContentDisposition等Metadata

```java
    storage.bucket("mybucket").key("data/upload/pic.jpg").entity(new File("C:\\My Picture.jpg")).upload();
    
```

更多的设置

```java
	storage.bucket("mybucket").
		key("data/upload/mydata").
		contentType("application/octet-stream").
		contentMD5("******FAKE******").
		contentLanguage("en");
```
=======
#sndacs
>>>>>>> 48a5241745a5be479d1f2ea4a1af78981a13b15d

[盛大云存储服务](http://www.grandcloud.cn/product/ecs)的官方Java SDK。

## 特点
1. DSL(Fluent Interface)风格的API，简洁易用
2. 支持Access Policy Language，通过DSL风格的API生成Bucket Policy
3. 支持大文件上传（大于5GB），对于大于5GB文件则自动通过Multipart Upload上传，对开发者透明
4. 提供了独立的签名与认证模块
5. 支持HTTPS安全网络访问
6. 无需配置Endpoint，自动支持多盛大云存储服务的多IDC
7. 支持限速传输

## 下载

1. [snda-cloud-storage-java-sdk-2.0.0-SNAPSHOT.jar](http://www.grandcloud.cn/product/ecs) 二进制发布包
2. [snda-cloud-storage-java-sdk-2.0.0-SNAPSHOT.zip](http://www.grandcloud.cn/product/ecs) 包含源代码，第三方依赖，Java Doc的发布包

## Maven依赖
```xml
    <dependency>
      <groupId>com.snda</groupId>
      <artifactId>snda-cloud-storage-java-sdk</artifactId>
      <version>2.0.0</version>
    </dependency>
```
## 使用
盛大云存储服务Java SDK提供了DSL风格的API，简单易用，只需要两部即可访问盛大云存储服务

### 构建SNDAStorage对象

所有对盛大云存储服务的访问都是通过SNDAStorage对象开始，我们通过SNDAStorageBuilder来构建一个SNDAStorage：
```java
	SNDAStorage storage = new SNDAStorageBuilder().credential(yourAccessKeyId, yourSecretAccessKey).build();
```
更多的设置:(通常来说，盛大云存储SDK已经对各项参数进行了调优，用户不需要改动过多的参数)
```java
	SNDAStorage storage = new SNDAStorageBuilder().
		credential(yourAccessKeyId, yourSecretAccessKey).
		https(). 						//启用HTTPS
		bytesPerSecond(64 * 1024).		//限制每秒传输速率为64KB
		connectionTimeout(10 * 1000).	//设置ConnectionTimeout为10秒
		soTimeout(30 * 1000).			//设置SoTimeout为30秒
		build();
```		

SNDAStorage对象内部维护了一组HTTP连接池，在不使用该对象时，应该调用其destory方法销毁该对象，
```java
	storage.destory();
```

### Bucket相关的操作
```java
    storage.bucket("mybucket").create();			//在默认的Location节点中创建名为mybucket的Bucket
    
    storage.bucket("mybucket").exist();				//判断名为mybucket的Bucket是否存在
    
    storage.bucket("mybucket").location(Location.HUADONG_1).create();	//在华东一节点中创建名为mybucket的Bucket
    
    storage.bucket("mybucket").location().get();	//查看该Bucket的Location

	storage.bucket("mybucket").delete();			//删除该Bucket
```

### 上传数据
```java
    storage.bucket("mybucket").object("data/upload/pic.jpg").entity(new File("d:\\user\\my_picture.jpg")).upload();
```

### 上传数据时自定义Metadata:
```java
	storage.bucket("mybucket").
		key("data/upload/mydata").
		contentType("application/octet-stream").	
		contentMD5("ABCDEFGUVWXYZ").
		contentLanguage("en").
		metadata("x-snda-meta-foo", "bar").
		metadata("x-snda-meta-creation", new DateTime().
		metadata("x-snda-meta-author", "wangzijian@snda.com").
		entity(2048L, inputStream).
		upload();
```
### 下载数据
```java
	SNDAObject object = null;
	try {
		object = storage.bucket("mybucket").object("data/upload/pic.jpg").download();
		read(object.getContent());
	} finally {
		Closeables.closeQuietly(object);
	}
```
SNDAObject实现了java.io.Closeable接口，其内部持有了代表Object内容的InputStream，需要在使用完毕时正确的关闭。

### 下载至本地硬盘:
```java
	storage.bucket("mybucket").object("data/upload/pic.jpg").download().to(new File("~/download/my_pic.jpg"));
```

### 条件下载(Conditional GET)
```java
	storage.bucket("mybucket").object("music/norther.mp3").ifModifedSince(new DateTime(2012, 10, 7, 20, 0, 0)).download();
```

### 分段下载(Range GET)
```java
	storage.bucket("mybucket").object("music/norther.mp3").range(1000, 5000).download();
```

### 获取Metadata(HEAD Object) 
```java
	SNDAObjectMetadata metadata = storage.bucket("mybucket").object("music/norther.mp3").head();
```


## Copyright

Copyright (c) 2012 grandcloud.cn.
All rights reserved.
