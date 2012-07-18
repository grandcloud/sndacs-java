#sndacs

sndacs library provides access to [SNDA Cloud Storage](http://www.grandcloud.cn/product/ecs).

## Maven dependency

    <dependency>
      <groupId>com.snda</groupId>
      <artifactId>snda-cloud-storage-java-sdk</artifactId>
      <version>1.0.0</version>
    </dependency>

## Usage

### Initialize the credential

    ProviderCredentials credentials = 
                    new SNDACredentials("accessKey", "secretKey");

### Initialize the storage service

    CSService service = new RestCSService(credentials);

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
