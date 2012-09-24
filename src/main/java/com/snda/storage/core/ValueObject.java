package com.snda.storage.core;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;

/**
 * 
 * @author wangzijian@snda.com
 * 
 */
public abstract class ValueObject {

	public static final ToStringStyle TO_STRING_STYLE = new BestToStringStyle();

	@Override
	public boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}

	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public String toString() {
		return ToStringBuilder.reflectionToString(this, TO_STRING_STYLE);
	}

	private static class BestToStringStyle extends ToStringStyle {

		private static final long serialVersionUID = 1L;

		public BestToStringStyle() {
			setUseShortClassName(true);
			setUseIdentityHashCode(false);
		}

		@Override
		public void append(StringBuffer buffer, String fieldName, Object value, Boolean fullDetail) {
			if (value != null) {
				super.append(buffer, fieldName, value, fullDetail);
			}
		}

		@Override
		public void append(StringBuffer buffer, String fieldName, Object[] array, Boolean fullDetail) {
			if (array != null && array.length > 0) {
				super.append(buffer, fieldName, array, fullDetail);
			}
		}
	}
}
