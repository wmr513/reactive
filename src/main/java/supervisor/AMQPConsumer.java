package supervisor;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;

import common.AMQPCommon;

public class AMQPConsumer {

	private Boolean active = false;

	public void start(Connection connection) {
		try {
			active = true;
			Channel channel = connection.createChannel();
			QueueingConsumer consumer = new QueueingConsumer(channel);
			channel.basicQos(1);
			channel.basicConsume("trade.eq.q", false, consumer);
			while (active) {
				QueueingConsumer.Delivery msg = consumer.nextDelivery();
				System.out.println("message received: " + new String(msg.getBody()));
				Thread.sleep(1000);
				channel.basicAck(msg.getEnvelope().getDeliveryTag(), false);
			}
			channel.close();
		} catch (Exception e) {
			e.printStackTrace();
		}	
	}	
	
	public void shutdown() {
		synchronized(active) {
			active = false;
		}
	}
}





