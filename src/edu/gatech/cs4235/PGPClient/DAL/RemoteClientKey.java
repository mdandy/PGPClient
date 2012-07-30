package edu.gatech.cs4235.PGPClient.DAL;

public class RemoteClientKey {
	
	private String username;
	private String key;
	
	public RemoteClientKey(String username, String key) {
		this.username = username;
		this.key = key;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
}
