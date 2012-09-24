package com.snda.storage.core;

import java.io.File;
import java.util.List;

import com.snda.storage.Location;
import com.snda.storage.SNDAObject;
import com.snda.storage.SNDAObjectMetadata;
import com.snda.storage.policy.Policy;
import com.snda.storage.xml.CompleteMultipartUploadResult;
import com.snda.storage.xml.CopyObjectResult;
import com.snda.storage.xml.CopyPartResult;
import com.snda.storage.xml.CreateBucketConfiguration;
import com.snda.storage.xml.InitiateMultipartUploadResult;
import com.snda.storage.xml.ListAllMyBucketsResult;
import com.snda.storage.xml.ListBucketResult;
import com.snda.storage.xml.ListMultipartUploadsResult;
import com.snda.storage.xml.ListPartsResult;
import com.snda.storage.xml.Part;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public interface StorageService {

	ListAllMyBucketsResult listBuckets();

	ListBucketResult listObjects(String bucket);

	ListBucketResult listObjects(String bucket, ListBucketCriteria criteria);

	ListMultipartUploadsResult listMultipartUploads(String bucket);

	ListMultipartUploadsResult listMultipartUploads(String bucket, ListMultipartUploadsCriteria criteria);

	boolean doesBucketExist(String bucket);

	Location getBucketLocation(String bucket);

	void createBucket(String bucket);

	void createBucket(String bucket, CreateBucketConfiguration createBucketConfiguration);

	void deleteBucket(String bucket);

	void setBucketPolicy(String bucket, Policy policy);

	Policy getBucketPolicy(String bucket);

	void deleteBucketPolicy(String bucket);

	void deleteObject(String bucket, String key);

	SNDAObjectMetadata headObject(String bucket, String key);

	SNDAObjectMetadata headObject(String bucket, String key, GetObjectRequest getObjectRequest);

	SNDAObject downloadObject(String bucket, String key);

	SNDAObject downloadObject(String bucket, String key, GetObjectRequest getObjectRequest);

	UploadObjectResult uploadObject(String bucket, String key, File file);

	UploadObjectResult uploadObject(String bucket, String key, UploadObjectRequest uploadObjectRequest);

	CopyObjectResult copyObject(String bucket, String key, CopyObjectRequest copyObjectRequest);

	InitiateMultipartUploadResult initiateMultipartUpload(String bucket, String key, ObjectCreation objectCreation);

	ListPartsResult listParts(String bucket, String key, String uploadId);

	ListPartsResult listParts(String bucket, String key, String uploadId, ListPartsCriteria criteria);

	UploadPartResult uploadPart(String bucket, String key, String uploadId, int partNumber, UploadPartRequest uploadPartRequest);

	CopyPartResult copyPart(String bucket, String key, String uploadId, int partNumber, CopyPartRequest copyPartRequest);

	CompleteMultipartUploadResult completeMultipartUpload(String bucket, String key, String uploadId, List<Part> parts);

	void abortMultipartUpload(String bucket, String key, String uploadId);

	void setCredential(Credential credential);

	Credential getCredential();
}
