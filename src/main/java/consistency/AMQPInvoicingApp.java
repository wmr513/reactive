package consistency;

import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import common.AMQPCommon;

public class AMQPInvoicingApp {

	private static Map<String, String> cache = new HashMap<String, String>();
	static {
		cache.put("123", "3,360");
	}

	public static void main(String[] args) throws Exception {
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("sync.q", true, consumer);
		displayCache();
		
		while (true) {
			QueueingConsumer.Delivery msg = consumer.nextDelivery();
			String body = new String(msg.getBody());
			System.out.println("synchronize message received: " + body);
			String[] parts = body.split(",");
			String cust = parts[0];
			long price = new Long(parts[2]).longValue();
			price = (long)(price - (price*.10));
			long cost = new Long(cache.get(cust).split(",")[1]).longValue() + price;
			long qty = new Long(cache.get(cust).split(",")[0]).longValue() + 1;
			cache.put(cust, qty + "," + cost);
			displayCache();
		}			
	}	
	
	private static void displayCache() {
		System.out.println("");
		for (Map.Entry<String, String> cust : cache.entrySet()) {
			String[] parts = cust.getValue().split(",");
			System.out.println(cust.getKey() + ": (" + parts[0] + ") " + parts[1]);
		}
	}
	
}





