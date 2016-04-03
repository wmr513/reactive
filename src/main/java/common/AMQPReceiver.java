package common;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.QueueingConsumer;

public class AMQPReceiver {

	public static void main(String[] args) throws Exception {
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicQos(1);
		channel.basicConsume("trade.eq.q", false, consumer);

		int numMsgs = args.length > 0 ? new Integer(args[0]).intValue() : 1;
		for (int i=0; i<numMsgs; i++) {
			QueueingConsumer.Delivery msg = consumer.nextDelivery();
			System.out.println("message received: " + new String(msg.getBody()));
			Thread.sleep(1000);
			channel.basicAck(msg.getEnvelope().getDeliveryTag(), false);
		}			
		
		AMQPCommon.close(channel);
	}	
}





