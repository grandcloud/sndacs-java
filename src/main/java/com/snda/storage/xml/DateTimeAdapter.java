package com.snda.storage.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import org.joda.time.DateTime;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class DateTimeAdapter extends XmlAdapter<String, DateTime> {

	@Override
	public String marshal(DateTime dateTime) throws Exception {
		return dateTime.toString();
	}

	@Override
	public DateTime unmarshal(String date) throws Exception {
		return new DateTime(date);
	}

}
