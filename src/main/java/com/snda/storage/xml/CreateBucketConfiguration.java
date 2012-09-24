package com.snda.storage.xml;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.snda.storage.Location;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
@XmlRootElement(name = "CreateBucketConfiguration")
public class CreateBucketConfiguration extends XMLEntity {

	@XmlElement(name = "LocationConstraint")
	@XmlJavaTypeAdapter(LocationAdapter.class)
	private Location locationConstraint;

	public CreateBucketConfiguration() {
	}

	public CreateBucketConfiguration(Location locationConstraint) {
		this.locationConstraint = locationConstraint;
	}

	public Location getLocationConstraint() {
		return locationConstraint;
	}

	public void setLocationConstraint(Location locationConstraint) {
		this.locationConstraint = locationConstraint;
	}

}
