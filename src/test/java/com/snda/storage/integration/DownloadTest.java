package com.snda.storage.integration;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;
import com.snda.storage.exceptions.CSServiceException;
import com.snda.storage.exceptions.ServiceException;
import com.snda.storage.security.ProviderCredentials;
import com.snda.storage.security.SNDACredentials;
import com.snda.storage.service.CSService;
import com.snda.storage.service.Constants;
import com.snda.storage.service.impl.rest.httpclient.RestCSService;
import com.snda.storage.service.model.CSBucket;
import com.snda.storage.service.model.CSObject;
import com.snda.storage.service.model.MultipartPart;
import com.snda.storage.service.model.MultipartUpload;
import com.snda.storage.service.model.StorageBucket;
import com.snda.storage.service.model.StorageObject;
import com.snda.storage.service.multi.cs.CSServiceEventListener;
import com.snda.storage.service.utils.Mimetypes;
import com.snda.storage.service.utils.MultipartUtils;

public class DownloadTest {
	
//	private static String access_key_id = "imyxgfeoxl46s3u42";
//	private static String access_key_secret = "NDkwODc1NjBjYjljNzAzZmFiMzk0MjFkZTU4YTNiODc=";
	
//	private static String access_key_id = "EStore";
//	private static String access_key_secret = "054e1b27-8d50-400f-bc43-17ba3ce3b8c1";
	
//	private static String access_key_id = "llap";
//	private static String access_key_secret = "80456636-1f1d-42d8-961c-5088fadb38db";
	
//	private static String access_key_id = "ku6";
//	private static String access_key_secret = "NDBkOTJmYjY1ZGJhNmRjZGRmNTc5NjgxYWJiODFjZGY=";
	
//	private static String access_key_id = "is6w0bih4lj9lto8t";
//	private static String access_key_secret = "YzA4ZmJjN2RhNGY0NWFkNDJiMzQxNDAzNTYwZjlkYzQ=";
	
//	private static String access_key_id = "itcr46cysly38prsa";
//	private static String access_key_secret = "YzIwNTk2NmM1NzgyYTNjMTI0NjMwMGQwYTU5YzQ0ODg=";
	
//	private static String access_key_id = "io5l217ij4ht6k4my";
//	private static String access_key_secret = "MDEwMzJkODQ2MThkZTJmMWIxNWYwYmQ4ZThhNzIyYTk=";
	
	private static String access_key_id = "2O8CN0HJ5BJ41EXSXJDKROQ8G";
	private static String access_key_secret = "Zjc4NjQzMDEtOTM0Zi00NzRmLTg2NDgtMzRlZmY1YzVkOTRl";
	
//	private static String access_key_id = "8A9N7XR0RTRNV0NQYWKP14V44";
//	private static String access_key_secret = "MWU0YjQ5NmUtNTJiYy00NDkxLTgyYmEtM2QyYjE5ZDA5YjAw";
	
//	private static String access_key_id = "is6w0bih4lj9lto8t";
//	private static String access_key_secret = "YzA4ZmJjN2RhNGY0NWFkNDJiMzQxNDAzNTYwZjlkYzQ=";
	
//	private static String access_key_id = "DH2TBAGSDAGA8YW47MV79I5AZ";
//	private static String access_key_secret = "ZWRiNDMwMzgtZThiZS00OTM2LWE3MWUtMGQ5YzdlODEwZTA0";
	
//	private static String access_key_id = "8CAPLLR8QTO4J8PGXOGO47FFV";
//	private static String access_key_secret = "NWJkNGI1MmUtMTgyMS00MGM2LTkyMzctYjBhNjEwNjBhMTA0";
	
	private static ProviderCredentials myCred;
	private static CSService myService;
	
	@Before
	public void before() {
		myCred = new SNDACredentials(access_key_id, access_key_secret);
		myService = new RestCSService(myCred);
//		myService.setHttpsOnly(true);
	}
	
	@Test
	public void createBucketTest() {
//		myService.createBucket("abcsdssdfsfa");
//		myService.getOrCreateBucket("abcsdssdfsfaa", "huadong-1");
	}
	
	@Test
	public void listAllBuckets() {
		CSBucket[] allBuckets = myService.listAllBuckets();
		for (CSBucket bucket : allBuckets) {
			System.out.println(bucket.getName() + ":" + bucket.getLocation());
		}
	}
	
	@Test
	public void uploadTest() throws NoSuchAlgorithmException, IOException, URISyntaxException {
		
		try {
			CSObject object = new CSObject(new File("/home/jiangwenhan/Desktop/empty_file"));
			myService.putObject("beijing", object);
		} catch (CSServiceException e) {
			if (e.getResponseCode() == 301) {
				System.out.println(e.getCSErrorCode());
				System.out.println(e.getCSErrorMessage());
				System.out.println(e.getCSErrorRequestId());
				System.out.println(e.getCSErrorEndpoint());
				URI uri = new URI(e.getCSErrorEndpoint());
				Constants.CS_DEFAULT_HOSTNAME = uri.getHost();
				CSObject object = new CSObject(new File("/home/jiangwenhan/Desktop/empty_file"));
				myService.putObject("beijing", object);
			}
		}
	}

	@Test
	public void downloadTest() throws IOException {
		
        StorageObject object = myService.getObject("inote_backup", "4edf1cdfba4881aa0e000003_Partition_1__2012-03-01_15-50-52.tar.gz");
        object.getDataInputStream(); 
        
        OutputStream out = new FileOutputStream("/tmp/4edf1cdfba4881aa0e000003_Partition_1__2012-03-01_15-50-52.tar.gz");
//        ByteStreams.copy(object.getDataInputStream(), out);
        
        object.getDataInputStream().close();
        out.close();
	}
	
	@Test
	public void listTest() {
		StorageBucket[] buckets = myService.listAllBuckets();
		for (StorageBucket bucket : buckets) {
			System.out.println(bucket);
		}
	}
	
	@Test
	public void listObjectTest() throws IOException {
		StorageObject[] objects = myService.listObjects("llap_log_backup_dnc", null, null, null, 1000L);
		for (StorageObject object : objects) {
			System.out.println("+++" + object.getName() + "+++");
			myService.deleteObject("llap_log_backup_dnc", object.getName());
		}
	}
	
	@Test
	public void list() {
//		CSObject[] objects = myService.listObjects("spring321123", "spring321123/delta", "/");
		CSObject[] objects = myService.listObjects("spring321123", "delta/", "/");
		for (CSObject object : objects) {
			System.out.println(object.getKey());
		}
	}
	
	@Test
	public void headObjectTest() throws NoSuchAlgorithmException, IOException {
		CSObject object = new CSObject("\n", "new_snapshot/1.JPG");
//		StorageObject headObject = myService.headObject("wuxi", "wolegequ/");
		myService.putObject("wuxi", object);
//		System.out.println("");
	}
	
	@Test
	public void putAndDeleteBucketTest() throws Exception {
//		CSBucket bucket = new CSBucket("ptolemaeus_policy_test");
//		myService.createBucket(bucket);
		
//		CSBucket bucket = myService.getBucket("ptolemaeus2");
//		System.out.println(bucket.getName());
//		System.out.println(bucket.getLocation());
//		System.out.println(bucket.getCreationDate());
		
//		System.out.println(myService.getBucketLocation("ptolemaeus2"));
		
		CSBucket[] buckets = myService.listAllBuckets();
		for (CSBucket bucket : buckets) {
			System.out.println(bucket.getName());
			System.out.println(bucket.getLocation());
			System.out.println(bucket.getCreationDate());
		}
		
//		myService.deleteBucket(new CSBucket("ptolemaeus2", "huabei-1"));
//		CSObject[] objects = myService.listObjects("ptolemaeus_beijing_test");
//		for (CSObject object : objects) {
//			System.out.println(object.getKey());
//		}
	}
	
	@Test
	public void fuck() {
		System.out.println(1 > 2 ? 1 : 2);
	}
	
	@Test
	public void testRandom() {
		int i = 100;
		while (i-- > 0) {
			System.out.println(new Random().nextInt(2));
		}
	}
	
	@Test
	public void testBucketPolicy() throws IOException {
//		BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream("src/test/resources/complex_in_single_line.policy")));
//		String policyDocument = br.readLine();
//		myService.setBucketPolicy("ptolemaeus_policy_test", policyDocument);
		
		String policy = myService.getBucketPolicy("ptolemaeus_policy_test");
		System.out.println(policy);
		
//		myService.deleteBucketPolicy("ptolemaeus_policy_test");
//		
//		policy = myService.getBucketPolicy("ptolemaeus_policy_test");
//		System.out.println(policy);
	}
	
	@Test
	public void testMultiUpload() throws NoSuchAlgorithmException, IOException {
//		MultipartUpload multipartUpload = myService.multipartStartUpload("ptolemaeus_policy_test", "multiupload_key", null);
//		System.out.println(multipartUpload.getUploadId());
//		
//		myService.multipartAbortUpload(multipartUpload);
		
//		List<MultipartUpload> listUploads = myService.multipartListUploads("ptolemaeus_policy_test");
//		System.out.println(listUploads.size());
//		for (MultipartUpload upload : listUploads) {
//			System.out.println(upload.getObjectKey() + ":" + upload.getUploadId());
//		}
		
//		MultipartUpload upload = new MultipartUpload("24J5HP43RVUR1K7OSEOTQN4YE", "ptolemaeus_policy_test", "multiupload_key");
//		List<MultipartPart> listParts = myService.multipartListParts(upload);
//		System.out.println(listParts.size());
		
//		MultipartCompleted multipartCompleteUpload = myService.multipartCompleteUpload(upload);
//		System.out.println();
		
//		CSObject object = new CSObject(new File("/home/jiangwenhan/Downloads/4398783.1763629797.pdf"));
		CSBucket bucket = new CSBucket("pop");
		CSObject object = new CSObject(bucket, "Ronan Keating - Turn It On.ape");
		object.setDataInputStream(new FileInputStream("/home/jiangwenhan/Downloads/Ronan Keating - Turn It On.ape"));
//		object.setDataInputFile(new File("/home/jiangwenhan/Downloads/Ronan Keating - Turn It On.ape"));
//		System.out.println(object.getMd5HashAsHex());
		object.setLastModifiedDate(new Date());
		File file = new File("/home/jiangwenhan/Downloads/Ronan Keating - Turn It On.ape");
		long length = file.length();
//		myService.putObjectAsMultipart("pop", object, MultipartUtils.MIN_PART_SIZE, length);
	}
	
//	@Test
//	public void testFuck() throws Exception {
//		String uri = "hdfs://llapmerger02:16196/archive-index/archive.543.2012_06_13_10_49_01.delta/10.176.16.241_9101_snda_storage_db_543_delta_range_0_table.543.delta";
//		FileSystem fs = FileSystem.get(URI.create(uri), new Configuration());
//		FSDataInputStream fsDataInputStream = fs.open(new Path(uri));
//		CSBucket bucket = new CSBucket("pop");
//		CSObject object = new CSObject(bucket, "fuckfuck");
//		object.setDataInputStream(fsDataInputStream);
//		object.setLastModifiedDate(new Date());
//		long totalSize = fs.getFileStatus(new Path(uri)).getLen();
//		myService.putObjectAsMultipart("pop", object, MultipartUtils.MIN_PART_SIZE, totalSize, fs, uri);
//	}
	
	@Test
	public void testputobject() throws NoSuchAlgorithmException, IOException {
		CSBucket bucket = new CSBucket("wuxiabc");
		CSObject object = new CSObject(bucket, "123/fuckdata");
		object.setDataInputFile(new File("/home/jiangwenhan/Desktop/RabbitMQReporterTest.java"));
//		object.setDataInputStream(new FileInputStream("/home/jiangwenhan/Desktop/RabbitMQReporterTest.java"));
		File file = new File("/home/jiangwenhan/Desktop/RabbitMQReporterTest.java");
		object.setContentLength(file.length());
		CSObject putObject = myService.putObject("wuxiabc", object);
		
//		StorageObject storageObject = myService.headObject("ptolemaeus_policy_test", "fuckkey4");
//		for (Entry entry : storageObject.getMetadataMap().entrySet()) {
//			System.out.println(entry.getKey() + ":" + entry.getValue());
//		}
	}
	
	@Test
	public void testCreateSignedUrl() {
		Date expiryTime = new Date(System.currentTimeMillis() + 100000L);
		System.out.println(myService.createSignedPutUrl("ptolemaeus_policy_test", "fuckkey4", null, expiryTime));
		System.out.println(myService.createSignedGetUrl("ptolemaeus_policy_test", "fuckkey4", expiryTime));
		System.out.println(myService.createSignedHeadUrl("ptolemaeus_policy_test", "fuckkey4", expiryTime));
		System.out.println(myService.createSignedDeleteUrl("ptolemaeus_policy_test", "fuckkey4", expiryTime));
	}
	
	@Test
	public void testPutObjectThroughSignedUrl() throws Exception {
		long abc = System.currentTimeMillis() + 10000L;
		Date expiryTime = new Date(abc);
		CSBucket bucket = new CSBucket("wuxi");
//		CSObject csobject = new CSObject(bucket, new File("/home/jiangwenhan/Desktop/Rails3 in Action.pdf"));
		CSObject csobject = new CSObject(bucket, "Rails3 in Action.pdf");
//		csobject.setDataInputStream(new FileInputStream("/home/jiangwenhan/Desktop/Rails3 in Action.pdf"));
//		csobject.setDataInputFile(new File("/home/jiangwenhan/Desktop/Rails3 in Action.pdf"));
//		csobject.addMetadata("Date", expiryTime);
		Map<String, Object> metadataMap = csobject.getMetadataMap();
		for (Entry<String, Object> meta : metadataMap.entrySet()) {
			System.out.println("+++" + meta.getKey() + ":" + meta.getValue() + "+++");
		}
		String putObjectSignedUrl = myService.createSignedPutUrl("wuxi", "Rails3 in Action.pdf", metadataMap, expiryTime);
		System.out.println(putObjectSignedUrl);
		try {
			myService.putObjectWithSignedUrl(putObjectSignedUrl, csobject);
		} catch (ServiceException e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetObjectThroughSignedUrl() {
		long abc = System.currentTimeMillis() + 10000L;
		Date expiryTime = new Date(abc);
		String signedGetUrl = myService.createSignedGetUrl("ptolemaeus_policy_test", "fuckkey2", expiryTime);
		try {
			CSObject csObject = myService.getObjectWithSignedUrl(signedGetUrl);
			BufferedReader br = new BufferedReader(new InputStreamReader(csObject.getDataInputStream()));
			System.out.println(br.readLine());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testGetObjectDetailThroughSignedUrl() {
		long abc = System.currentTimeMillis() + 10000L;
		Date expiryTime = new Date(abc);
		String signedheadUrl = myService.createSignedHeadUrl("ptolemaeus_policy_test", "fuckkey4", expiryTime);
		try {
			CSObject csObject = myService.getObjectDetailsWithSignedUrl(signedheadUrl);
			for (Entry entry : csObject.getMetadataMap().entrySet()) {
				System.out.println(entry.getKey() + ":" + entry.getValue());
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void testDeleteObjectThroughSignedUrl() {
		long abc = System.currentTimeMillis() + 10000L;
		Date expiryTime = new Date(abc);
		String signedDeleteUrl = myService.createSignedDeleteUrl("ptolemaeus_policy_test", "fuckkey4", expiryTime);
		try {
			myService.deleteObjectWithSignedUrl(signedDeleteUrl);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	@Test
	public void dateTest() {
		long time = System.currentTimeMillis();
		System.out.println(time);
		long secondTime = time / 1000;
		System.out.println(secondTime * 1000);
		System.out.println(new Date(time));
		System.out.println(new Date(secondTime * 1000));
//		System.out.println(ServiceUtils.formatRfc822Date(new Date(System.currentTimeMillis())));
	}
	
	@Test
	public void testMultiStart() throws Exception {
		MultipartUtils multiutils = new MultipartUtils(5 * 1024 * 1024);
		
		CSBucket bucket = new CSBucket("pop");
		CSObject object = new CSObject(bucket, "Ronan Keating fuck - Turn It On.ape");
		object.setDataInputFile(new File("/home/jiangwenhan/Downloads/Ronan Keating - Turn It On.ape"));
		
		List<StorageObject> objects = Lists.newArrayList();
		objects.add(object);
		
		CSServiceEventListener eventListener;
		multiutils.uploadObjects("pop", myService, objects, null);
	}
	
	@Test
	public void test12321() {
		List<MultipartUpload> multipartListUploads = myService.multipartListUploads("pop");
		for (MultipartUpload part : multipartListUploads) {
			System.out.println(part.getObjectKey());
			System.out.println(part.getUploadId());
		}
		System.out.println(multipartListUploads.size());
	}
	
	@Test
	public void location() {
		String bucketLocation = myService.getBucketLocation("wuxi");
		System.out.println(bucketLocation);
	}
	
	@Test
	public void putBucket() {
		CSBucket bucket = new CSBucket("wolegequasdjfkj", "huabei-1");
		myService.createBucket(bucket);
	}
	
	@Test
	public void headObject() {
		CSObject object = myService.getObjectDetailsWithSignedUrl("http://storage.sdcloud.cn/wuxi/README?SNDAAccessKeyId=3BC3RG5AL3EE6FMZWC8YFL2ZY&Expires=1341554786&Signature=8zSfyv03tMv8MccrRWUpHktHpkY%3D");
		System.out.println(object.getKey());
		System.out.println(object.getBucketName());
	}
	
	@Test
	public void testListparts() {
		MultipartUpload upload = new MultipartUpload("E9FOGLECBKAB1RDEJSGP4Z5J2", "wuxi", "b2250b23");
		List<MultipartPart> multipartListParts = myService.multipartListParts(upload);
		System.out.println(multipartListParts.size());
	}
	
	@Test
	public void testReduceRedondency() throws NoSuchAlgorithmException, IOException {
		CSObject object = new CSObject("caonimeia.txt", "Endpoint");
		myService.putObject("wuxi", object);
	}
	
	@Test
	public void testCopy() {
		CSObject object = new CSObject("wocao_Winter2.jpg");
		object.setContentType(Mimetypes.getInstance().getMimetype("wocao_Winter2.jpg"));
		try {
//			myService.copyObject("beijing", "Dirt_3_OST.rar", "wuxi", object, true);
//			myService.copyObject("beijing", "Dirt_3_OST.rar", "wuxi", object, true);
			Calendar ifModifiedSince = Calendar.getInstance();
			String[] ifMatchTags = {"\"8d12d96a4b0d4de40f481e2c7d98e7b6\""};
			String[] ifNoneMatchTags = ifMatchTags;
			myService.copyObject("beijing", "Dirt_3_OST.rar", 
					"wuxi", object, false, 
					null, null, null, ifNoneMatchTags);
		} catch (ServiceException e) {
			e.printStackTrace();
			System.out.println();
			System.out.println(e.getErrorRequestId());
			System.out.println(e.getErrorCode());
			System.out.println(e.getErrorMessage());
		}
	}
	
	@Test
	public void headCopy() {
		CSObject object = myService.headObject("wuxi", "wocao_Winter1.jpg");
		for (Entry entry: object.getMetadataMap().entrySet()) {
			System.out.println(entry.getKey() + ":" + entry.getValue());
		}
	}
	
	@Test
	public void testCalendar() {
		System.out.println(System.currentTimeMillis());
		System.out.println(Calendar.getInstance());
	}
	
	
}
