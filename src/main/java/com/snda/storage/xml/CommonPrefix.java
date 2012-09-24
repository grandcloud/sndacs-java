package com.snda.storage.xml;

import javax.xml.bind.annotation.XmlElement;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class CommonPrefix extends XMLEntity {

	@XmlElement(name = "Prefix")
	private String prefix;

	public CommonPrefix() {
	}

	public CommonPrefix(String prefix) {
		this.prefix = prefix;
	}

	public String getPrefix() {
		return prefix;
	}

	public void setPrefix(String prefix) {
		this.prefix = prefix;
	}

}
