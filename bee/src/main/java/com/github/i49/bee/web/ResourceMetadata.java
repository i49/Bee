package com.github.i49.bee.web;

import java.util.Date;

public class ResourceMetadata {

	private Locator location;
	private Locator redirectLocation;
	private MediaType mediaType;
	private Date lastModified;
	
	public static Builder builder() {
		return new Builder();
	}
	
	private ResourceMetadata() {
	}
	
	public Locator getLocation() {
		return location;
	}

	public Locator getRedirectLocation() {
		return redirectLocation;
	}

	public Locator getFinalLocation() {
		if (redirectLocation != null) {
			return redirectLocation;
		} else {
			return location;
		}
	}
	
	public MediaType getMediaType() {
		return mediaType;
	}
	
	public Date getLastModified() {
		return lastModified;
	}

	public static class Builder {
		
		private Locator location;
		private Locator redirectLocation;
		private MediaType mediaType;
		private Date lastModified;

		public Builder setLocation(Locator location) {
			this.location = location;
			return this;
		}

		public Builder setRedirectLocation(Locator redirectLocation) {
			this.redirectLocation = redirectLocation;
			return this;
		}
		
		public Builder setMediaType(MediaType mediaType) {
			this.mediaType = mediaType;
			return this;
		}
		
		public Builder setLastModified(Date lastModified) {
			this.lastModified = lastModified;
			return this;
		}
		
		public ResourceMetadata build() {
			ResourceMetadata result = new ResourceMetadata();
			result.location = location;
			result.redirectLocation = redirectLocation;
			result.mediaType = mediaType;
			result.lastModified = lastModified;
			return result;
		}
	}
}
