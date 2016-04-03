package supervisor;

import java.util.ArrayList;
import java.util.List;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import common.AMQPCommon;

public class AMQPSupervisor {
	
	private List<AMQPConsumer> consumers = 
	   new ArrayList<AMQPConsumer>();
	Connection connection;
	
	public void run() throws Exception {
		System.out.println("Starting supervisor");
		Channel channel = AMQPCommon.connect();
		connection = channel.getConnection();
		startConsumer();
		while (true) {
			long queueDepth = channel.messageCount("trade.eq.q");

			long consumersNeeded = queueDepth/2;
			long diff = Math.abs(consumersNeeded - consumers.size());
			for (int i=0;i<diff;i++) {
				if (consumersNeeded > consumers.size()) 
					startConsumer();
				else 
					stopConsumer();
			}			
			Thread.sleep(1000);
		}
	}
	
	private void startConsumer() {
		System.out.println("Starting consumer...");
		AMQPConsumer consumer = new AMQPConsumer();
		consumers.add(consumer);
	    new Thread() { 
	    	public void run() { 
	    		try {
	    			consumer.start(connection);
	    		} catch (Exception e) {
	    			e.printStackTrace();
	    		}
	    }}.start();
	}
	
	private void stopConsumer() {
		if (consumers.size() > 1) {
			System.out.println("Removing consumer...");
			AMQPConsumer consumer = consumers.get(0);
			consumer.shutdown();
			consumers.remove(consumer);
		}
	}

	public static void main(String[] args) throws Exception {
		AMQPSupervisor app = new AMQPSupervisor();
		app.run();
	}
}





