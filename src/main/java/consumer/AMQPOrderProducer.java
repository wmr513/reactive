package consumer;

import com.rabbitmq.client.Channel;
import common.AMQPCommon;

/**
 * produce book, cd, or dvd order
 */
public class AMQPOrderProducer {

	private Channel channel;
	
	public static void main(String[] args) throws Exception {
		AMQPOrderProducer app = new AMQPOrderProducer();
		app.produceMessages();
	}

	//produce 4 movies, 1 cd, 1 book
	private void produceMessages() throws Exception {		
		channel = AMQPCommon.connect();
		placeOrder("book", "Seveneves");
		placeOrder("book", "Anathem");
		placeOrder("book", "Cryptonomicon");
		placeOrder("book", "Snow Crash");
		placeOrder("music", "Boulevard of Broken Dreams");
		placeOrder("movie", "Breaking Bad Season 1");
		AMQPCommon.close(channel);
	}
	
	private void placeOrder(String type, String item) throws Exception {
		String msgBody = item;
		System.out.println("ordering " + type + " " + msgBody);
		channel.basicPublish("orders.dx", type, null, msgBody.getBytes());
	}
}










