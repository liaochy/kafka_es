package com.sohu.tv.m.kafka.es.resolve;

import java.io.Serializable;

@SuppressWarnings("serial")
public class SerializSparkLog implements Serializable {

	private String host;
	
	private String path;
	
	private java.util.Map<String, String> fields;

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public java.util.Map<String, String> getFields() {
		return fields;
	}

	public void setFields(java.util.Map<String, String> fields) {
		this.fields = fields;
	}

}
