package com.snda.storage.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.joda.time.DateTime;

import com.snda.storage.Location;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public class BucketSummary extends XMLEntity {

	@XmlElement(name = "Name")
	private String name;

	@XmlElement(name = "Location")
	@XmlJavaTypeAdapter(LocationAdapter.class)
	private Location location = Location.DEFAULT;
	
	@XmlElement(name = "CreationDate")
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private DateTime creation;
	
	public BucketSummary() {
	}

	public BucketSummary(String name, Location location, DateTime creation) {
		this.name = name;
		this.location = location;
		this.creation = creation;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Location getLocation() {
		return location;
	}

	public void setLocation(Location location) {
		this.location = location;
	}

	public DateTime getCreation() {
		return creation;
	}

	public void setCreation(DateTime creation) {
		this.creation = creation;
	}
	
	
}
