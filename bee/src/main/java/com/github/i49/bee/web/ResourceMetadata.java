package com.github.i49.bee.web;

import java.util.Date;

public class ResourceMetadata {

	private Locator location;
	private MediaType mediaType;
	private Date lastModified;
	private String contentEncoding;
	private long contentLength;
	
	public static Builder builder() {
		return new Builder();
	}
	
	public Locator getLocation() {
		return location;
	}

	public MediaType getMediaType() {
		return mediaType;
	}
	
	public Date getLastModified() {
		return lastModified;
	}

	public String getContentEncoding() {
		return contentEncoding;
	}
	
	public long getContentLength() {
		return contentLength;
	}

	public static class Builder {
		
		private Locator location;
		private MediaType mediaType;
		private Date lastModified;
		private String contentEncoding;
		private long contentLength = -1;

		public Builder setLocation(Locator location) {
			this.location = location;
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
		
		public Builder setContentEncoding(String contentEncoding) {
			this.contentEncoding = contentEncoding;
			return this;
		}
		
		public Builder setContentLength(long contentLength) {
			this.contentLength = contentLength;
			return this;
		}
		
		public ResourceMetadata build() {
			ResourceMetadata result = new ResourceMetadata();
			result.location = location;
			result.mediaType = mediaType;
			result.lastModified = lastModified;
			result.contentEncoding = contentEncoding;
			result.contentLength = contentLength;
			return result;
		}
	}
}
