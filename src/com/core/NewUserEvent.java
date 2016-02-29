package com.core;

/* Class stores details of an event called newUsers
 * Objects of this class are instantiated when a new event of
 * topic newUsers have been added by producers
 * */
public class NewUserEvent {
	private String eventParam;
	private String eventValue;

	public NewUserEvent(String eventParam,String eventValue){
		this.eventParam = eventParam;
		this.eventValue = eventValue;
	}
	
	public String getEventValue() {
		return eventValue;
	}

	public String getEventParam() {
		return eventParam;
	}
	
}
