package com.snda.storage.core;


/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class CopyPartRequest extends ValueObject {

	private CopySource copySource;
	private Range copySourceRange;
	private Condition copyCondition = new Condition();

	public CopyPartRequest withCopySource(CopySource copySource) {
		setCopySource(copySource);
		return this;
	}
	
	public CopyPartRequest withCopyCondition(Condition copyCondition) {
		setCopyCondition(copyCondition);
		return this;
	}
	
	public CopyPartRequest withCopySourceRange(Range range) {
		setCopySourceRange(range);
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

	public Range getCopySourceRange() {
		return copySourceRange;
	}

	public void setCopySourceRange(Range copySourceRange) {
		this.copySourceRange = copySourceRange;
	}

}
