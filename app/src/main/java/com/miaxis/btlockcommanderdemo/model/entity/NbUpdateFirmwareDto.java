package com.miaxis.btlockcommanderdemo.model.entity;

public class NbUpdateFirmwareDto {
	private String version;
	private String data;
	private boolean local;
	
	public String getVersion() {
		return version;
	}
	public String getData() {
		return data;
	}
	public void setVersion(String version) {
		this.version = version;
	}
	public void setData(String data) {
		this.data = data;
	}

	public boolean isLocal() {
		return local;
	}

	public void setLocal(boolean local) {
		this.local = local;
	}
}
