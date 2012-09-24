package com.snda.storage.xml;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import com.snda.storage.Location;
import com.snda.storage.core.ValueObject;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "LocationConstraint")
public final class LocationConstraint {

	@XmlValue
	@XmlJavaTypeAdapter(LocationAdapter.class)
	private Location value = Location.DEFAULT;

	public LocationConstraint() {
	}

	public LocationConstraint(Location value) {
		this.value = value;
	}

	public Location getValue() {
		return value;
	}

	public void setValue(Location value) {
		this.value = value;
	}

	@Override
	public final boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}

	@Override
	public final int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, ValueObject.TO_STRING_STYLE);
	}

}
