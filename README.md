#盛大云存储服务Java SDK

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
通常来说，盛大云存储SDK已经对各项参数进行了调优，用户不需要改动过多的参数。

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

### List buckets

    CSBucket[] buckets = service.listAllBuckets();

### Add bucket

    CSBucket csBucket = new CSBucket("bucket_name");
    service.createBucket(bucket);

### Get bucket informations

    CSBucket csBucket = service.getBucket("bucket_name");

### Delete bucket

    service.deleteBucket("bucket_name");

### Upload object from file

    CSObject csObject = new CSObject(new File("filepath/file"));
    service.putObject("bucket_name", csObject);

### Get object informations

    CSObject csObject = service.headObject("bucket_name", "object_name");

### Upload object as reduced redundency storage class

    CSObject csObject = new CSObject(new File("filepath/file"));
    csObject.setStorageClass("REDUCED_REDUNDANCY");
    service.putObject("bucket_name", csObject);

### Copy object

    CSObject copyObject = new CSObject("copy.jpg");
    copyObject.setContentType(Mimetypes.getInstance().getMimetype("copy.jpg"));
    service.copyObject("source_bucket", "source_object", "dst_bucket", copyObject, true);

### Download object

    CSObject csObject = service.getObject("bucket_name", "object_name");
    InputStream in = csObject.getDataInputStream();

### Delete object

    service.deleteObject("bucket_name", "object_name");

### Get the existence of object in bucket

    boolean exist = service.isObjectInBucket("bucket_name", "object_name");

### Get bucket, if not exist, then create

    CSBucket csBucket = service.getOrCreateBucket("bucket_name");

### Get bucket location

    String location = service.getBucketLocation("bucket_name");

### List objects

    CSObject[] objects = service.listObjects("bucket_name");

### Use https

    service.setHttpsOnly(true);

### Get bucket policy

    String policyXml = service.getBucketPolicy("bucket_name");

### Set bucket policy

    service.setBucketPolicy("bucket_name", policyXml);

### Delete bucket policy

    service.deleteBucketPolicy("bucket_name");

### Initialize multiupload

    MultipartUpload multipart = service.multipartStartUpload("bucket_name", 
                                                             "object_name",
                                                             metadata);

### Upload part

    service.multipartUploadPart(multipart, partNumber, partObject);

### List parts that have been uploaded

    service.multipartListParts(multipart);

### List multipart uploads that have been started within a bucket and have not yet been completed or aborted

    service.multipartListUploads("bucket_name");

### Complete multiupload

    service.multipartCompleteUpload(multipart);

### Abort multiupload

    service.multipartAbortUpload(multipart);

### Create signed url

    String signedPutUrl = service.createSignedPutUrl("bucket_name",
                                                     "object_name",
                                                     headersMap,
                                                     expireDate);

    String signedGetUrl = service.createSignedGetUrl("bucket_name",
                                                     "object_name",
                                                     expireDate);

    String signedHeadUrl = service.createSignedHeadUrl("bucket_name",
                                                       "object_name",
                                                       expireDate);

    String signedDeleteUrl = service.createSignedDeleteUrl("bucket_name",
                                                           "object_name",
                                                           expireDate);
                                                           
### Upload object through signed url

    CSObject csObject = new CSObject("object_name", "data");
    csObject.addMetadata("Date", expireDate);
    String signedPutUrl = service.createSignedPutUrl("bucket_name", 
                                                     "object_name",
                                                     csObject.getMetadataMap(), 
                                                     expireDate);
    service.putObjectWithSignedUrl(signedPutUrl, csObject);
    
### Download object through signed url

    String signedGetUrl = service.createSignedGetUrl("bucket_name",
                                                     "object_name",
                                                     expireDate);
    CSObject csObject = service.getObjectWithSignedUrl(signedGetUrl);
    
### Get informations of object through signed url

    String signedHeadUrl = service.createSignedHeadUrl("bucket_name",
                                                       "object_name",
                                                       expireDate);
    CSObject csObject = service.getObjectDetailsWithSignedUrl(signedHeadUrl);
    
### Delete object through signed url

    String signedDeleteUrl = service.createSignedDeleteUrl("bucket_name",
                                                           "object_name",
                                                           expireDate);
    service.deleteObjectWithSignedUrl(signedDeleteUrl);
    
### Catch response error exception

    String dataString = "Text for MD5 hashing...";
    CSObject csObject = new CSObject("Testing MD5 Hashing", dataString);
    csObject.setContentType("text/plain");
    byte[] md5Hash = ServiceUtils.computeMD5Hash(dataString.getBytes());
    try {
        csObject.addMetadata("Content-MD5", "123");
        service.putObject("testBucket", object);
    } catch (CSServiceException e) {
        System.out.println(e.getErrorCode() + "\n" + 
                           e.getErrorMessage() + "\n" +
                           e.getErrorResource() + "\n" +
                           e.getErrorRequestId());
    }

## Copyright

Copyright (c) 2012 grandcloud.cn.
All rights reserved.
