package consumer;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import common.AMQPCommon;

public class AMQPMovieConsumer {

	public static void main(String[] args) throws Exception {
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("movie.q", true, consumer);

		while (true) {
			QueueingConsumer.Delivery msg = consumer.nextDelivery();
			System.out.println("PROCESSING MOVIE ORDER: " + new String(msg.getBody()));
			Thread.sleep(2000);
		}			
	}	
}
