package com.snda.storage.xml;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
@XmlRootElement(name = "CopyPartResult")
public class CopyPartResult extends CopyResult {

	public CopyPartResult() {
		super();
	}

	public CopyPartResult(String entityTag, DateTime lastModified) {
		super(entityTag, lastModified);
	}

}
