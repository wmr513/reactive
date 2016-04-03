package dispatcher;

import java.util.Random;

import com.rabbitmq.client.Channel;
import common.AMQPCommon;

/**
 * Continuously produce messages every half second
 */
public class AMQPThreadProducer {
	
	public static void main(String[] args) throws Exception {
		AMQPThreadProducer app = new AMQPThreadProducer();
		app.produceMessages();
	}
	
	private void produceMessages() throws Exception {		
		Channel channel = AMQPCommon.connect();
		while (true) {
			long shares = ((long) ((new Random().nextDouble() * 4000) + 1));
			String text = "BUY AAPL " + shares + " SHARES";
			byte[] message = text.getBytes();
			String routingKey = "trade.eq.q";
			System.out.println("sending trade: " + text);
			channel.basicPublish("", routingKey, null, message);
			Thread.sleep(500);
		}
	}
}









