package com.snda.storage.core;

import java.io.File;

import com.snda.storage.Entity;
import com.snda.storage.core.support.FileEntity;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class UploadObjectRequest extends ValueObject {

	private Entity entity;
	private String contentMD5;
	private ObjectCreation objectCreation = new ObjectCreation();

	public UploadObjectRequest withEntity(Entity entity) {
		setEntity(entity);
		return this;
	}

	public UploadObjectRequest withContentMD5(String contentMD5) {
		setContentMD5(contentMD5);
		return this;
	}
	
	public UploadObjectRequest withObjectCreation(ObjectCreation objectCreation) {
		setObjectCreation(objectCreation);
		return this;
	}

	public String getContentMD5() {
		return contentMD5;
	}

	public void setContentMD5(String contentMD5) {
		this.contentMD5 = contentMD5;
	}

	public ObjectCreation getObjectCreation() {
		return objectCreation;
	}

	public void setObjectCreation(ObjectCreation objectCreation) {
		this.objectCreation = objectCreation;
	}

	public Entity getEntity() {
		return entity;
	}

	public void setEntity(Entity entity) {
		this.entity = entity;
		if (entity instanceof FileEntity) {
			File file = ((FileEntity) entity).getFile();
			getObjectCreation().setContentType(getContentType(file));
			getObjectCreation().setContentDisposition(getContentDisposition(file));
		}
	}

	private String getContentType(File file) {
		return FileTypes.getInstance().getMimetype(file);
	}

	private String getContentDisposition(File file) {
		return new StringBuilder().append("attachment; filename=\"").append(file.getName()).append("\"").toString();
	}
}
