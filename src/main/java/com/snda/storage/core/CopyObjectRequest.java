package com.snda.storage.core;



/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class CopyObjectRequest extends ValueObject {

	private CopySource copySource;
	private Condition copyCondition = new Condition();
	private ObjectCreation objectCreation = new ObjectCreation();
	private MetadataDirective metadataDirective;

	public CopyObjectRequest withCopySource(CopySource copySource) {
		setCopySource(copySource);
		return this;
	}

	public CopyObjectRequest withCopyCondition(Condition copyCondition) {
		setCopyCondition(copyCondition);
		return this;
	}
	
	public CopyObjectRequest withObjectCreation(ObjectCreation objectCreation) {
		setObjectCreation(objectCreation);
		return this;
	}

	public CopyObjectRequest withMetadataDirective(MetadataDirective metadataDirective) {
		setMetadataDirective(metadataDirective);
		return this;
	}

	public CopySource getCopySource() {
		return copySource;
	}

	public void setCopySource(CopySource copySource) {
		this.copySource = copySource;
	}

	public Condition getCopyCondition() {
		return copyCondition;
	}

	public void setCopyCondition(Condition copyCondition) {
		this.copyCondition = copyCondition;
	}

	public ObjectCreation getObjectCreation() {
		return objectCreation;
	}

	public void setObjectCreation(ObjectCreation objectCreation) {
		this.objectCreation = objectCreation;
	}

	public MetadataDirective getMetadataDirective() {
		return metadataDirective;
	}

	public void setMetadataDirective(MetadataDirective metadataDirective) {
		this.metadataDirective = metadataDirective;
	}

}
