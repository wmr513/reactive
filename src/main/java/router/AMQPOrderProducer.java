package router;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.rabbitmq.client.AMQP;
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
		AMQP.BasicProperties header = createHeader(type);
		System.out.println("ordering " + type + " " + msgBody);
		channel.basicPublish("", "order.q", header, msgBody.getBytes());
	}

	private AMQP.BasicProperties createHeader(String msgType) {
		AMQP.BasicProperties.Builder header = new AMQP.BasicProperties().builder();
		Map<String, Object> props = new HashMap<String, Object>();
		props.put("type", msgType);
		header.headers(props);
		return header.build();
	}
	
}









