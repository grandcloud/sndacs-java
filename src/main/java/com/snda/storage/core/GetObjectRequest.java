package com.snda.storage.core;


/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class GetObjectRequest extends ValueObject {

	private Range range;
	private Condition condition = new Condition();
	private ResponseOverride responseOverride = new ResponseOverride();

	public GetObjectRequest withRange(Range range) {
		setRange(range);
		return this;
	}

	public GetObjectRequest withCondition(Condition condition) {
		setCondition(condition);
		return this;
	}
	
	public GetObjectRequest withResponseOverride(ResponseOverride responseOverride) {
		setResponseOverride(responseOverride);
		return this;
	}

	public Range getRange() {
		return range;
	}

	public void setRange(Range range) {
		this.range = range;
	}

	public Condition getCondition() {
		return condition;
	}

	public void setCondition(Condition condition) {
		this.condition = condition;
	}

	public ResponseOverride getResponseOverride() {
		return responseOverride;
	}

	public void setResponseOverride(ResponseOverride responseOverride) {
		this.responseOverride = responseOverride;
	}

}
