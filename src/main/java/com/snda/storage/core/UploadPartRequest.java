package com.snda.storage.core;

import com.snda.storage.Entity;


/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class UploadPartRequest extends ValueObject {

	private Entity entity;
	private String contentMD5;

	public UploadPartRequest withEntity(Entity entity) {
		setEntity(entity);
		return this;
	}

	public UploadPartRequest withContentMD5(String contentMD5) {
		setContentMD5(contentMD5);
		return this;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
	}

	public String getContentMD5() {
		return contentMD5;
	}

	public void setContentMD5(String contentMD5) {
		this.contentMD5 = contentMD5;
	}

}
