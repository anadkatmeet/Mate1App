package com.core;

/*Class stores details of consumer like sessionId and offset
 * This class will be used by broker to create instances of consumer 
 * when they register for the first time
 * */
public class Consumer {
 	private String sessionId;
	private int offset;
	
	public Consumer(String id, int offset){
		sessionId = id;
		this.offset = offset;
	}
	
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	
	
}
