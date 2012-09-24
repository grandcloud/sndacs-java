package com.snda.storage.policy;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.google.common.collect.ImmutableList;
import com.snda.storage.core.ValueObject;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class Action extends ValueObject {

	public static final String ALL = "*";
	public static final String LIST_ALL_MY_BUCKETS = "storage:ListAllMyBuckets";
	public static final String LIST_BUCKET = "storage:ListBucket";
	public static final String CREATE_BUCKET = "storage:CreateBucket";
	public static final String DELETE_BUCKET = "storage:DeleteBucket";
	public static final String GET_BUCKET_LOCATION = "storage:GetBucketLocation";
	public static final String PUT_BUCKET_POLICY = "storage:PutBucketPolicy";
	public static final String GET_BUCKET_POLICY = "storage:GetBucketPolicy";
	public static final String DELETE_BUCKET_POLICY = "storage:DeleteBucketPolicy";
	public static final String PUT_BUCKET_LOGGING = "storage:PutBucketLogging";
	public static final String GET_BUCKET_LOGGING = "storage:GetBucketLogging";
	public static final String LIST_BUCKET_MULTIPART_UPLOADS = "storage:ListBucketMultipartUploads";
	public static final String GET_OBJECT = "storage:GetObject";
	public static final String PUT_OBJECT = "storage:PutObject";
	public static final String DELETE_OBJECT = "storage:DeleteObject";
	public static final String LIST_MULTIPART_UPLOAD_PARTS = "storage:ListMultipartUploadParts";
	public static final String ABORT_MULTIPART_UPLOAD = "storage:AbortMultipartUpload";

	private final String name;
	private final List<String> values;

	public static Action action(String... actions) {
		return new Action("Action", ImmutableList.copyOf(actions));
	}

	public static Action notAction(String... actions) {
		return new Action("NotAction", ImmutableList.copyOf(actions));
	}

	public Action(String name, List<String> values) {
		this.name = checkNotNull(name);
		this.values = ImmutableList.copyOf(checkNotNull(values));
	}

	public String getName() {
		return name;
	}

	public List<String> getValues() {
		return values;
	}

}
