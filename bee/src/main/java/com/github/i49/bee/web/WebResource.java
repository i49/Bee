package com.github.i49.bee.web;

import java.util.Date;

public class WebResource {

	private ResourceContent content;
	
	private Locator location;
	private Locator redirectLocation;
	private MediaType mediaType;
	private Date lastModified;
	
	public static Builder builder() {
		return new Builder();
	}
	
	public Locator getLocation() {
		return location;
	}

	public Locator getRedirectLocation() {
		return redirectLocation;
	}

	public boolean isRedirected() {
		return (redirectLocation != null);
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

	public boolean hasContent() {
		return (content != null);
	}
	
	public ResourceContent getContent() {
		return content;
	}
	
	public void setContent(ResourceContent content) {
		this.content = content;
	}

	private WebResource() {
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
		
		public WebResource build() {
			WebResource result = new WebResource();
			result.location = location;
			result.redirectLocation = redirectLocation;
			result.mediaType = mediaType;
			result.lastModified = lastModified;
			return result;
		}
	}
}
