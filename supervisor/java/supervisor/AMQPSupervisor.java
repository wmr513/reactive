package supervisor;

import java.util.ArrayList;
import java.util.List;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import common.AMQPCommon;

public class AMQPSupervisor {
	
	private List<AMQPConsumer> consumers = new ArrayList<AMQPConsumer>();
	Connection connection;
	
	public void run(long keepAlive) throws Exception {
		System.out.println("Starting supervisor");
		Channel channel = AMQPCommon.connect();
		connection = channel.getConnection();
		startConsumer();	

		long check = 1000;
		long checkCounter = 0;
		while (true) {
			long queueDepth = channel.messageCount("trade.eq.q");
			long consumersNeeded = new Double(queueDepth/2).longValue();
			long diff = Math.abs(consumersNeeded - consumers.size());
			if (consumersNeeded > consumers.size()) {
				for (int i=0;i<diff;i++) {
					startConsumer();
				}
			} else if (checkCounter >= keepAlive) {
				checkCounter = 0;
				for (int i=0;i<diff;i++) {
					stopConsumer();
				}
			}
			checkCounter +=check;
			Thread.sleep(check);
		}
	}
	
	private void startConsumer() {
		System.out.println("Starting consumer...");
		AMQPConsumer consumer = new AMQPConsumer();
		consumers.add(consumer);
		new Thread(()->consumer.start(connection)).start();
	}
	
	private void stopConsumer() throws Exception {
		if (consumers.size() > 1) {
			System.out.println("Removing consumer...");
			AMQPConsumer consumer = consumers.get(0);
			consumer.shutdown();
			consumers.remove(consumer);
		}
	}

	public static void main(String[] args) throws Exception {
		AMQPSupervisor app = new AMQPSupervisor();
		java.util.Scanner input = new java.util.Scanner(System.in);
	    System.out.print("Keep Alive (ms): ");
	    long keepAlive = input.nextLong();
		input.close();
		app.run(keepAlive);
	}
}





