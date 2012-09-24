package com.snda.storage.xml;

import javax.xml.bind.annotation.XmlRootElement;

import org.joda.time.DateTime;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
@XmlRootElement(name = "CopyObjectResult")
public class CopyObjectResult extends CopyResult {

	public CopyObjectResult() {
		super();
	}

	public CopyObjectResult(String entityTag, DateTime lastModified) {
		super(entityTag, lastModified);
	}

}
