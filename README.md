#盛大云存储服务Java SDK

[盛大云存储服务](http://www.grandcloud.cn/product/ecs)Java SDK由盛大官方提供，开发者可以利用该工具包实现：

1. 管理Bucket信息
2. 上传与下载Object数据
3. 生成与设置Bucket Policy
4. 生成预签名(Presigned)的可公开访问的URL

## 特点
1. DSL(Fluent Interface)风格的API，简洁易用
2. 支持Access Policy Language，通过DSL风格的API生成Bucket Policy
3. 支持大文件上传（最大5TB），自动通过Multipart Upload机制上传大文件，对开发者透明
4. 提供了独立的签名与认证模块供开发者使用
5. 支持HTTPS安全网络访问
6. 无需配置Endpoint，自动支持多盛大云存储服务的多IDC
7. 支持限速传输

## 下载
目前最新的版本是2012年09月24日发布的2.0.0：

1. [snda-cloud-storage-java-sdk-2.0.0-SNAPSHOT.jar](https://github.com/downloads/grandcloud/sndacs-java/snda-cloud-storage-java-sdk-2.0.0.jar)	二进制发布包
2. [snda-cloud-storage-java-sdk-2.0.0-SNAPSHOT.zip](https://github.com/downloads/grandcloud/sndacs-java/snda-cloud-storage-java-sdk-2.0.0.zip)	包含源代码，第三方依赖，Javadoc等内容的发布包

## Maven

```xml
<dependency>
	<groupId>com.snda</groupId>
	<artifactId>snda-cloud-storage-java-sdk</artifactId>
	<version>2.0.0</version>
</dependency>
```

## 使用
盛大云存储服务Java SDK提供了DSL风格的Java API，易于上手，简单高效。其核心为SNDAStorage对象，开发者通过该对象提供的多种方法来访问盛大云存储服务。

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
### 列出所有的Bucket
```java
for (BucketSummary each : storage.listBuckets()) {
	String name = each.getName();
}
```

### Bucket相关操作
```java
storage.bucket("mybucket").create();				//在默认的Location节点中创建名为mybucket的Bucket
    
storage.bucket("mybucket").location(Location.HUADONG_1).create();	//在华东一节点中创建名为mybucket的Bucket
    
storage.bucket("mybucket").location().get();		//查看该Bucket的Location

storage.bucket("mybucket").delete();				//删除该Bucket

storage.bucket("mybucket").policy(myPolicy).set();	//设置新的Bucket Policy

storage.bucket("mybucket").policy().get();			//获取Bucket Policy

storage.bucket("mybucket").policy().delete();		//删除Bucket Policy

ListBucketResult result = storage.					//根据条件列出Objects
	bucket("mybucket").
	prefix("upload/").
	delimiter("/")
	maxKeys(25).
	listObjects();									
	
ListMultipartUploadsResult result = storage.		//根据条件列出Multipart Uploads
	bucket("mybucket").
	prefix("data/").
	maxUploads(50).
	listMultipartUploads();							
```

### Object相关的操作
上传数据
```java
storage.
	bucket("mybucket").
	object("data/upload/pic.jpg").
	entity(new File("d:\\user\\my_picture.jpg")).
	upload();
```

自定义Metadata
```java
storage.
	bucket("mybucket").
	object("data/upload/mydata").
	reducedRedundancy().
	contentType("application/octet-stream").	
	contentMD5("ABCDEFGUVWXYZ").
	contentLanguage("en").
	metadata("x-snda-meta-foo", "bar").
	metadata("x-snda-meta-creation", new DateTime()).
	metadata("x-snda-meta-author", "wangzijian@snda.com").
	entity(2048L, inputStream).
	upload();
```
下载数据
```java
SNDAObject object = null;
try {
	object = storage.bucket("mybucket").object("data/upload/pic.jpg").download();
	read(object.getContent());
} finally {
	Closeables.closeQuietly(object);
}
```
SNDAObject实现了java.io.Closeable接口，其内部持有了代表Object内容的InputStream，需要在使用完毕时关闭。

下载至本地硬盘
```java
storage.
	bucket("mybucket").
	object("data/upload/pic.jpg").
	download().
	to(new File("~/download/my_pic.jpg"));
```

条件下载(Conditional GET)
```java
storage.
	bucket("mybucket").
	object("norther.mp3").
	ifModifedSince(new DateTime(2012, 10, 7, 20, 0, 0)).
	download();
```

分段下载(Range GET)
```java
storage.
	bucket("mybucket").
	object("norther.mp3").
	range(1000, 5000).
	download();
```

获取Object信息与Metadata(HEAD Object) 
```java
SNDAObjectMetadata metadata = storage.
	bucket("mybucket").
	object("music/norther.mp3").
	head();
```

更新Object信息与Metadata
```java
storage.
	bucket("mybucket").
	object("music/norther.mp3").
	reducedRedundancy().
	contentType("audio/mpeg").
	metadata("x-snda-meta-nation", "Finland").
	update();
```

复制Object
```java
storage.
	bucket("mybucket").
	object("book/english.txt").
	copySource("otherbucket", "data/edu/main.txt");
	replaceMetadata().
	contentType("text/plain").
	metadata("x-snda-meta-author", "Jack Jackson").
	update();
```
## Bucket Policy
盛大云存储SDK提供了强大的Bucket Policy构建器，开发者可以轻易生成和设置所需要的Bucket Policy。

允许匿名用户下载该Bucket中的所有数据，但限定User-Agent为Android或IOS，防盗链Referer设置为*.mycompany.com/*
```java
Statement statement = Statement.allow().anyone().perform("snda:GetObject").to("srn:snda:storage:::mybucket/*").
	where(userAgent().equals("Android", "IOS")).
	and(referer().equals("*.mycompany.com/*")).
	identifed("public-get-object");

storage.bucket("mybucket").policy(new Policy().
	withRandomId().
	withStatement(statement)).
	set();
```
许匿名用户下载该Bucket中的所有数据，限定请求的时间必须在2012年10月1日0点至2012年10月8日0点之间：
```java
Statement.allow().anyone().perform("snda:GetObject").to("srn:snda:storage:::mybucket/*").
	where(currentTime().greaterThan(new DateTime(2012, 10, 1, 0, 0, 0))).
	and(currentTime().lessThan(new DateTime(2012, 10, 8, 0, 0, 0))).
	identifed("public-get-object-with-time-restriction");
```
设置请求的IP必须在指定的"192.168.176.0/24"范围内，且链接为https安全链接：
```java
Statement.allow().anyone().perform("snda:GetObject").to("srn:snda:storage:::mybucket/*").
	where(sourceIp().ipAddress("192.168.176.0/24")).
	and(secureTransport().bool(true)).
	identifed("public-get-object-with-connection-restriction");

```

## 生成预签名的URI
盛大云存储提供了一种基于查询字串(Query String)的认证方式，即通过预签名(Presigned)的方式，为要发布的Object生成一个带有认证信息的URI，并将它分发给第三方用户来实现公开访问。

SDK中提供了PresigendURIBuilder来构造预签名URI。
```java
URI uri = storage.presignedURIBuilder().
	bucket("mybucket").
	object("hello_world.mp4").
	expires(new DateTime().plusMinutes(5))
	builder();

```
生成的URI如下：
```
http://storage-huadong-1.sdcloud.cn/mybucket/hello_world.mp4?Expires=1348044780&SNDAAccessKeyId=norther&Signature=SJawXv5QdQHcFrTqnx3RpmTN9WI%3D
```

## Entity
Entity代表要上传的Object的内容，由内容与长度组成，接口定义如下：
```java
public interface Entity extends InputSupplier<InputStream> {

	long getContentLength();

	InputStream getInput() throws IOException;
}
```
***getContentLength***方法返回该Entity的长度，盛大云存储服务要求上传的数据必须事先指定其长度，最大不得超过5TB。

***getInput***方法继承自[Google Guava](http://code.google.com/p/guava-libraries/)的InputSupplier。
InputSupplier代表Entity的内容，是一个打开InputStream的回调(Callback)。
在云存储SDK的不同模块之间，我们只传递InputSupplier的引用，而不是直接传递InputStream。
这是一种高效并且灵活的使用流的方式，因为只有在必要的时候，应用才会调用InputSupplier的getInput方法来打开一个新的InputStream，并保证该InputStream在使用完毕时能被正确的关闭。

下面的样例中，盛大云存储SDK只在必须要的情况下，才会调用getInput来打开流。
```java
object.
	bucket("mybucket").
	object("key").
	contentType("video/mp4").
	entity(65535, new InputSupplier<InputStream>() {
		@Override
		public InputStream getInput() throws IOException {
			return openStream();
		}
	}).
	upload();

```
云存储SDK提供了3种默认的Entity实现，包括InputSupplierEntity, InputStreamEntity,与 FileEntity

参考FileEntity的实现：
```java
public class FileEntity implements Entity {

	private final File file;

	public FileEntity(File file) {
		this.file = checkNotNull(file);
	}

	@Override
	public long getContentLength() {
		return file.length();
	}

	@Override
	public InputStream getInput() throws IOException {
		return new FileInputStream(file);
	}
}
```

***注意*** InputStreamEntity并不关闭其持有的InputStream对象，这样的做法符合IO Stream使用的最佳实现，即：***关闭自己打开的流***。

样例如下：
```java
InputStream inputStream = null;
try {
	inputStream = openInputStream(); //用户应负责关闭自己所打开的流，盛大云存储SDK并不会关闭用户传入的InputStream对象
	storage.bucket("mybucket").object("key").entity(256, inputStream).upload();
} finally {
	Closeables.closeQuietly(inputStream);
}

```

## Multipart Upload API
开发这使用盛大云存储SDK上传文件时，SDK会透明的使用Multipart Upload实现对大文件上传，一般情况下用户不需要自己来使用Multipart Upload API。

若开发者有自己使用Multipart Upload的需求，可以参看下面的使用样例：

```java
InitiateMultipartUploadResult result = storage.				//初始化Multipart Upload
	bucket("mybucket").
	object("blob").
	initiateMultipartUpload();

String uploadId = result.getUploadId();						//获得Multipart Upload Id

storage.													//上传Part
	bucket("mybucket").
	object("blob").
	multipartUpload(uploadId).
	partNumber(1).
	entity(new File("/user/data/bin1")).
	upload();

storage.													//复制Part
	bucket("mybucket").
	object("blob").
	multipartUpload(uploadId).
	partNumber(2).
	copySource("otherbucket", bigdata).
	copySourceRange(255, 65335);
	copy();
	
storage.													//完成Multipart Upload
	bucket("mybucket").
	object("blob").
	multipartUpload(uploadId).
	part(1, "ETag1").
	part(2, "ETag2").
	complete();
	
storage.													//放弃Multipart Upload
	bucket("mybucket").
	object("blob").
	multipartUpload(uploadId).
	abort();
	
storage.													//列出未完成的Parts
	bucket("mybucket").
	object("blob").
	multipartUpload(uploadId).
	partNumberMarker(10).
	maxParts(5).
	listParts();
```

## Exception
盛大云存储SDK会将云存储服务返回的Error转换成统一的异常层次：SNDAServiceException，用户可以基于该类型来实现诸如错误处理与诊断等操作。

```java
public class SNDAServiceException extends RuntimeException {

	public int status() 			//获得错误代表的HTTP状态码(Status)

	public String getRequestId()	//获得请求的ID，当错误发生时，开发者可以将该ID记录下来并告知盛大云存储服务来快速诊断错误

	public DateTime getDate()		//获得异常发生的时间
	
	public String getCode()			//获得错误码，对应Error结构中的Code元素，HEAD请求时，该值为null
	
	public String getResource()		//获得错误对应的资源，对应Error结构中的Resource元素，HEAD请求时，该值为null
	
	public String getErrorMessage()	//获得错误消息，对应Error结构中的Message元素，HEAD请求时，该值为null
	
}
```

除了标准的错误信息外，用户还可以获得额外的错误信息：
```java

try {
	doSomething();
} catch (SNDAServiceException e) {
	if ("SignatureDoesNotMatch".equals(e.getCode()) {
		String signatureProvided = e.getError().get("SignatureProvided");
	}
}

```

## 依赖
盛大云存储SDK依赖以下的第三方库:
<table>
	<tbody>
	<tr>
		<th>依赖</th>
		<th>描述</th>
		<th>网址</th>
	</tr>
	<tr>
		<td>
		Google Guava
		</td>
		<td>
		Google提供的Java基础类库，提供了函数式编程，并发，集合操作等多种基础功能
		</td>
		<td>
		http://code.google.com/p/guava-libraries/
		</td>
	</tr>
	<tr>
		<td>
		Joda Time
		</td>
		<td>
		一套关于时间的类库，已被收入至JDK 7中
		</td>
		<td>
		http://joda-time.sourceforge.net/
		</td>
	</tr>
	<tr>
		<td>
		SLF4J
		</td>
		<td>
		功能强大的日志框架
		</td>
		<td>
		http://www.slf4j.org/
		</td>
	</tr>
	<tr>
		<td>
		Apache Commons-Lang
		</td>
		<td>
		用来实现一些基础的操作，例如Object hashcode与equals方法的实现
		</td>
		<td>
		http://commons.apache.org/lang/
		</td>
	</tr>
	<tr>
		<td>
		Apache Commons-Codec
		</td>
		<td>
		用来进行一些诸如Base64之类的编码算法
		</td>
		<td>
		http://commons.apache.org/codec/
		</td>
	</tr>
	<tr>
		<td>
		Apache HTTP Client
		</td>
		<td>
		用来实现HTTP协议与网络数据的传输
		</td>
		<td>
		http://hc.apache.org/httpclient-3.x/
		</td>
	</tr>
	<tr>
		<td>
		Jackson
		</td>
		<td>
		著名的JSON格式序列化工具，只有在使用Bucket Policy时需要
		</td>
		<td>
		http://jackson.codehaus.org/
		</td>
	</tr>
	</tbody>
</table>
## Copyright

Copyright (c) 2012 grandcloud.cn.
All rights reserved.
