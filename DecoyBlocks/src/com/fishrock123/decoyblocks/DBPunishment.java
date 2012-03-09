package com.fishrock123.decoyblocks;

public class DBPunishment {
	private byte b;
	private String name;
	
	public DBPunishment(Byte b, String name) {
		this.b = b;
		this.name = name;
	}
	
	public byte getTrigger() {
		return b;
	}
	
	public String getName() {
		return name;
	}
}
