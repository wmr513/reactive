package producerflow;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import common.AMQPCommon;

/**
 * Continuously consume messages every 2 seconds
 */
public class AMQPContConsumer {

	public static void main(String[] args) throws Exception {
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicQos(1);
		channel.basicConsume("trade.eq.q", false, consumer);

		while (true) {
			QueueingConsumer.Delivery msg = consumer.nextDelivery();
			System.out.println("message received: " + new String(msg.getBody()));
			Thread.sleep(2000);
			channel.basicAck(msg.getEnvelope().getDeliveryTag(), false);
		}			
	}	
}





