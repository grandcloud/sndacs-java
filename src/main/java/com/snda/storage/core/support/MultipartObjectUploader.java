package com.snda.storage.core.support;
import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.snda.storage.ByteUnit.GB;
import static com.snda.storage.ByteUnit.MB;
import static com.snda.storage.ByteUnit.TB;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.collect.Lists;
import com.google.common.io.Closeables;
import com.google.common.io.LimitInputStream;
import com.snda.storage.Entity;
import com.snda.storage.core.StorageService;
import com.snda.storage.core.UploadObjectRequest;
import com.snda.storage.core.UploadObjectResult;
import com.snda.storage.core.UploadPartRequest;
import com.snda.storage.core.UploadPartResult;
import com.snda.storage.xml.InitiateMultipartUploadResult;
import com.snda.storage.xml.Part;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class MultipartObjectUploader implements ObjectUploader {

	private static final Logger LOGGER = LoggerFactory.getLogger(MultipartObjectUploader.class);
	
	private static final long DEFAULT_PART_SIZE = 100 * MB;
	private static final long MAX_OBJECT_SIZE = 5 * TB;
	
	private final StorageService storageService;
	
	private long partSize = DEFAULT_PART_SIZE;

	public MultipartObjectUploader(StorageService storageService) {
		this.storageService = checkNotNull(storageService);
	}

	@Override
	public UploadObjectResult putObject(String bucket, String key, UploadObjectRequest putObjectRequest) {
		Entity entity = putObjectRequest.getEntity();
		long contentLength = entity.getContentLength();
		LOGGER.info("Object Content-Length: {}", contentLength);
		checkArgument(contentLength <= MAX_OBJECT_SIZE, "Object is too large for uploading");
		if (contentLength <= partSize) {
			return storageService.uploadObject(bucket, key, putObjectRequest);
		}
		InitiateMultipartUploadResult multipartUpload = storageService.initiateMultipartUpload(bucket, key, putObjectRequest.getObjectCreation());
		LOGGER.info("Multipart Upload: {}", multipartUpload);
		try {
			String eTag = multipartUpload(multipartUpload, entity);
			return new UploadObjectResult(eTag);
		} catch (RuntimeException e) {
			tryAbort(multipartUpload);
			throw e;
		}
	}

	private String multipartUpload(InitiateMultipartUploadResult multipartUpload, Entity entity) {
		InputStream inputStream = null;
		try {
			inputStream = entity.getInput();
			return doMultipartUpload(multipartUpload, entity.getContentLength(), inputStream);
		} catch (IOException e) {
			throw new UncheckedIOException("Faield to read entity", e);
		} finally {
			Closeables.closeQuietly(inputStream);
		}
	}

	private String doMultipartUpload(InitiateMultipartUploadResult multipartUpload, long contentLength, final InputStream inputStream) {
		List<Part> parts = Lists.newArrayList();
		for (int partNumber = 1; partNumber <= contentLength / partSize; partNumber++) {
			UploadPartResult result = uploadPart(
					multipartUpload, 
					partNumber, 
					partSize, 
					new LimitInputStream(inputStream, partSize));
			parts.add(new Part(partNumber, result.getETag()));
		}
		long lastPartSize = contentLength % partSize;
		if (lastPartSize != 0) {
			int partNumber = parts.size() + 1;
			UploadPartResult result = uploadPart(
					multipartUpload, 
					partNumber, 
					lastPartSize,
					inputStream);
			parts.add(new Part(partNumber, result.getETag()));
		}
		return completeMultipartUpload(multipartUpload, parts);
	}

	private UploadPartResult uploadPart(
			InitiateMultipartUploadResult 
			multipartUpload, 
			int partNumber, 
			long contentLength,
			InputStream inputStream) {
		LOGGER.info("Upload Part: " + partNumber);
		return storageService.uploadPart(
				multipartUpload.getBucket(), 
				multipartUpload.getKey(), 
				multipartUpload.getUploadId(), 
				partNumber, 
				new UploadPartRequest().withEntity(new InputStreamEntity(contentLength, inputStream)));
	}
	
	private String completeMultipartUpload(InitiateMultipartUploadResult multipartUpload, List<Part> parts) {
		if (LOGGER.isInfoEnabled()) {
			LOGGER.info("Compelete Multipart Upload {} with Parts {}", multipartUpload.getUploadId(), parts);
		}
		return storageService.completeMultipartUpload(
				multipartUpload.getBucket(), 
				multipartUpload.getKey(),
				multipartUpload.getUploadId(),
				parts).getEntityTag();
	}
	
	private void tryAbort(InitiateMultipartUploadResult multipartUpload) {
		try {
			LOGGER.info("Abort Multipart Upload: {}", multipartUpload);
			storageService.abortMultipartUpload(
					multipartUpload.getBucket(), 
					multipartUpload.getKey(),
					multipartUpload.getUploadId());
		} catch (Exception e) {
			LOGGER.warn("Abort Multipart Upload Failed: " + multipartUpload, e);
		}
	}

	@Override
	public void setPartSize(long partSize) {
		checkArgument(partSize >= 5 * MB && partSize <= 5 * GB, "Part size must be between 5MB and 5GB");
		this.partSize = partSize;
	}
}
