package router;

import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import common.AMQPCommon;

public class AMQPRouter {
 
	private static Map<String, POJOOrderProcessor> processor = new HashMap<String, POJOOrderProcessor>(); 
	static {
		processor.put("book", new POJOBookProcessor());
		processor.put("music", new POJOMusicProcessor());
		processor.put("movie", new POJOMovieProcessor());
	};
	
	public static void main(String[] args) throws Exception {
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("order.q", true, consumer);

		while (true) {
			QueueingConsumer.Delivery msg = consumer.nextDelivery();
			String orderType = msg.getProperties().getHeaders().get("type").toString();
			String orderItem = new String(msg.getBody());
			processor.get(orderType).processOrder(orderItem);
			Thread.sleep(2000);
		}			
	}	
}





