package com.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.TimerTask;

/* Objects of this class are different topics that can be managed by broker
 * It also contains list of events which will be added by the broker
 * */
public class Topic {
	private String topicName;
	private ArrayList<NewUserEvent> eventList;			//List of events in this topic
	private ArrayList<Consumer> consumerList;			//List of consumers that are consuming from this topic
	
	public String getTopicName() {
		return topicName;
	}
	
	public Topic(String topicName){
		this.topicName = topicName;
		eventList = new ArrayList<>();
		consumerList = new ArrayList<>();
	}

	public ArrayList<NewUserEvent> getEventList() {
		return eventList;
	}

	public ArrayList<Consumer> getConsumerList() {
		return consumerList;
	}

	
	/*leetness challenge 1*/
	/*	This class will be invoked periodically,
	 *  first it checks the minimum value of offset in the current event list within all the consumers,
	 *  that means all the events before that minimum value have been consumed by all the consumers
	 *  then it removes all the events before that minimum offset
	 *  and finally it decrements the offset of all the consumers
	 * */
	public class CleanUpEvents extends TimerTask {
	    public void run() {
	    	
	    	int deleteCounter=getConsumersMinimumOffset();
	    	ArrayList<NewUserEvent> refactoredEventList = new ArrayList<>();
	    	
	    	synchronized (eventList) {	
	    		refactoredEventList = (ArrayList<NewUserEvent>) eventList.subList(0, deleteCounter);
	    		eventList = refactoredEventList;
			}
	    	refactorConsumersOffset(deleteCounter);
	    }
	    
	    /*returns minimum offset value from the list of current consumers and current events of this topic*/
	    private int getConsumersMinimumOffset(){
	    	int minimum = eventList.size()-1;
	    	synchronized (consumerList) {
	    		Iterator<Consumer> it = consumerList.iterator();
	    		while (it.hasNext()) {
					Consumer tempConsumer = (Consumer)it.next(); 
	    			minimum = (minimum<(tempConsumer.getOffset())) ? tempConsumer.getOffset() : minimum;
					
				}
	    		return minimum;
			}
	    }
	    
	    /*decrements offsets of all the consumers after deleting old events*/
	    private void refactorConsumersOffset(int decrementCounter){
	    	synchronized (consumerList) {
	    		Iterator<Consumer> it = consumerList.iterator();
	    		while (it.hasNext()) {
					Consumer tempConsumer = (Consumer)it.next(); 
	    			tempConsumer.setOffset(tempConsumer.getOffset()-decrementCounter);
				}
			}
	    }  
	}
	
}


