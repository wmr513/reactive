package producerflow;

import java.util.Random;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.QueueingConsumer;

import common.AMQPCommon;

/**
 * Continuously produce messages every 1 second based on delay value
 */
public class AMQPContProducerNoFlow {
	
	private Long delay = 1000l;
	private Connection connection;

	public static void main(String[] args) throws Exception {
		AMQPContProducerNoFlow app = new AMQPContProducerNoFlow();
		app.connection = AMQPCommon.connect().getConnection();
		app.produceMessages();
	}
	
	private void produceMessages() throws Exception {		
		Channel channel = connection.createChannel();
		while (true) {
			long shares = ((long) ((new Random().nextDouble() * 4000) + 1));
			String text = "BUY AAPL " + shares + " SHARES";
			byte[] message = text.getBytes();
			String routingKey = "trade.eq.q";
			System.out.println("sending trade: " + text);
			channel.basicPublish("", routingKey, null, message);
			Thread.sleep(delay);
		}
	}	
}









