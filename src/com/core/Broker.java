package com.core;

import java.util.ArrayList;
import java.util.UUID;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.json.simple.JSONObject;
import java.util.*;


@Path("/broker")
public class Broker {
	
	static public ArrayList<Topic> topicList = new ArrayList<>();
	
	
	/*sample call
	 * curl -v http://localhost:8080/Mate1Application/rest/broker/postEvent \
     -d 'one={"param1":"userId"}' \
     -d 'two={"param2":"8374"}'
     * Adds event to the current topic list
	 * and returns okay
	 * */
	@POST
	@Path("/postEvent")
	@Produces("application/json")
	public Response postEventByProducer(@FormParam("one") String param1, @FormParam("two") String param2){
		NewUserEvent event = new NewUserEvent(param1, param2);
		/*currently we have just one topic so I provided static topic name*/
		getTopicByName("newUsers").getEventList().add(event);
		return Response.ok().build();
	}
	
	
	@GET
	@Path("/getEvent")
	@Produces("application/json")
	public Response getEventByConsumer(@DefaultValue("0")@QueryParam("sessionID")String sessionID, @QueryParam("topic")String topic,
			@DefaultValue("-1")@QueryParam("offset")String offset){
		
		Topic currentTopic = getTopicByName(topic);
		
		/*if specified topic doesn't exists*/
		if (currentTopic==null) {
			//System.out.println("topic doesn't exist");
			return Response.status(400).build();
		}
		
		/*if there is no session id or the session id provided doesn't exists (expired), then
		 * create a new session for this consumer by generating random sessionid,
		 * return event with particular offset of new consumer
		 * increment offset of this new consumer
		 * */
		Response result = null;
		JSONObject jsonObj = new JSONObject();
		Consumer currentConsumer = getConsumerBySessionID(sessionID, currentTopic);
		if (sessionID.equals("0") || currentConsumer==null) {
			
			String uniqueID = UUID.randomUUID().toString();
			
			/*leetness challenge 2
			 * based on the input offset value, I am specifying the consumer offset
			 * default value is -1 so it will start from zero, otherwise from last event*/
			int userOffset = ((offset.equals("-1") ? 0 : (currentTopic.getEventList().size()-2)));
			
			currentConsumer = new Consumer(uniqueID, userOffset);
			currentTopic.getConsumerList().add(currentConsumer);
			
			/*get current event based on current consumer's offset*/
			NewUserEvent event = currentTopic.getEventList().get(currentConsumer.getOffset());
			
			jsonObj.put(event.getEventParam(), event.getEventValue());
			//returns session id as HEADER response
			result = Response.ok(jsonObj).header("sessionID", uniqueID).build();
		}
		/*fetch consumer with specified session id,
		 * check if there is a new event available,
		 * if yes-
		 * then return event with particular offset of this consumer
		 * and increment offset of this new consumer
		 * otherwise send 400 that no new event exist
		 * */
		else {
			/*get current event based on current consumer's offset*/
			NewUserEvent event = currentTopic.getEventList().get(currentConsumer.getOffset());
			
			jsonObj.put(event.getEventParam(), event.getEventValue());
			result = Response.ok(jsonObj).build();	
		}
		
		/*check if there are new events left, if yes  
		 * then increment consumer's offset
		 * otherwise call timeout to close this consumer's connection*/
		if (!(currentConsumer.getOffset()==(currentTopic.getEventList().size()-1))) {
			currentConsumer.setOffset(currentConsumer.getOffset()+1);
		}else{	//call timeout
			Timer timer = new Timer();
		    timer.schedule(new DisconnectConsumer(currentConsumer,currentTopic), 10 * 1000); //10 seconds
		}
		
		
		return result;
	}
	
	private Topic getTopicByName(String name){
		for(Topic temp : topicList){
			if (temp.getTopicName().equals(name)) {
				return temp;
			}
		}
		return null;
	}
	
	private Consumer getConsumerBySessionID(String id, Topic currentTopic){
		for(Consumer temp : currentTopic.getConsumerList()){
			if (temp.getSessionId().equals(id)) {
				return temp;
			}
		}
		return null;
	}
	
}

/*when this method is called, it will check if there are no new events then the offset of consumer
 * then it will remove consumer from consumer list*/
class DisconnectConsumer extends TimerTask {
    Consumer consumer;
    Topic topic;
    public DisconnectConsumer(Consumer currentConsumer, Topic currentTopic){
    	consumer = currentConsumer;
    	topic = currentTopic;
    }
    public void run() {
    	/* SYNCHRONIZE the consumer list and then
    	 * make sure no more events came in this certain period of time
    	 * if any new event came, then increment this consumer's offset*/
    	synchronized (topic.getConsumerList()) {
    		if(consumer.getOffset()==(topic.getEventList().size()-1)){
    			topic.getConsumerList().remove(consumer);
    		}else{
    			consumer.setOffset(consumer.getOffset()+1);
		}
		
	  }
    }
}


