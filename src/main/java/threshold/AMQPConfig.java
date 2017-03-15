package threshold;

import java.util.Arrays;
import java.util.List;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import common.AMQPCommon;

public class AMQPConfig {

	public void execute() throws Exception {
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("config.q", true, consumer);
		System.out.println("place_trade, 2000");
		
		while (true) {
			QueueingConsumer.Delivery message = consumer.nextDelivery();
			String msg = new String(message.getBody());
			System.out.println("UPDATE: place_trade, " + msg);
		}			
	}	
	
	public static void main(String[] args) throws Exception {
		new AMQPConfig().execute();
	}
}









