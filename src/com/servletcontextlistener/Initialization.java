package com.servletcontextlistener;

import java.util.Timer;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.core.Broker;
import com.core.NewUserEvent;
import com.core.Topic;

public class Initialization implements ServletContextListener{

	@Override
	public void contextDestroyed(ServletContextEvent arg0) {
		System.out.println("ended");
		
	}

	@Override
	public void contextInitialized(ServletContextEvent arg0) {
		System.out.println("started");	
		Topic topic1 = new Topic("newUsers");
		NewUserEvent event1 = new NewUserEvent("userid", "1111");
		NewUserEvent event2 = new NewUserEvent("userid", "2222");
		NewUserEvent event3 = new NewUserEvent("userid", "3333");
		NewUserEvent event4 = new NewUserEvent("userid", "4444");
		topic1.getEventList().add(event1);
		topic1.getEventList().add(event2);
		topic1.getEventList().add(event3);
		topic1.getEventList().add(event4);
		Broker.topicList.add(topic1);
		Timer timer = new Timer();
		timer.schedule(topic1.new CleanUpEvents(), 0, 60000);	//it will call clean up event every 60 seconds
		
		System.out.println("topiclist size"+Broker.topicList.size());
		
	}

}
