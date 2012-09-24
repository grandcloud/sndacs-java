package com.snda.storage.xml;

import javax.xml.bind.annotation.adapters.XmlAdapter;

import com.snda.storage.Location;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class LocationAdapter extends XmlAdapter<String, Location> {

	@Override
	public Location unmarshal(String v) throws Exception {
		return Location.parse(v);
	}

	@Override
	public String marshal(Location v) throws Exception {
		return v.toString();
	}

}
