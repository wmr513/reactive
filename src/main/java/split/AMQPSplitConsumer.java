package split;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import common.AMQPCommon;

public class AMQPSplitConsumer {

	public static void main(String[] args) throws Exception {
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicQos(1);
		channel.basicConsume(args[0], false, consumer);

		while (true) {
			QueueingConsumer.Delivery msg = consumer.nextDelivery(1000);
			if (msg == null) break;
			System.out.println("message received: " + new String(msg.getBody()));
			Thread.sleep(100);
			channel.basicAck(msg.getEnvelope().getDeliveryTag(), false);
		}
		
		System.exit(0);
	}	
}





