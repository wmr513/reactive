package consistency;

import java.util.HashMap;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import common.AMQPCommon;

@SuppressWarnings("serial")
public class AMQPOrderEntryApp {

	private static Map<String, Map<String, Integer>> cache = new HashMap<String, Map<String, Integer>>();
	static {
		cache.put("123", new HashMap<String, Integer>() {{
			put("Lumber", 2);
			put("Concrete", 1);
		}});
	}
	
	private static Map<String, Long> prices = new HashMap<String, Long>();
	static {
		prices.put("Lumber", 100L);
		prices.put("Concrete", 200L);
	}

	public void execute() throws Exception {
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("order.q", true, consumer);
		displayCache();
		
		while (true) {
			QueueingConsumer.Delivery msg = consumer.nextDelivery();
			String order = new String(msg.getBody());
			System.out.println("order received: " + order);
			placeOrder(order);
			sendEvent(channel, order);
			displayCache();
		}					
	}	
	
	private void placeOrder(String order) {
		String[] parts = order.split(",");
		String cust = parts[0];
		String item = parts[1];
		if (cache.get(cust).containsKey(item)) {
			cache.get(cust).put(item, cache.get(cust).get(item)+1);
		} else {
			cache.get(cust).put(item, 1);
		}
	}
	
	private void sendEvent(Channel channel, String order) throws Exception {
		String item = order.split(",")[1];
		String msg = order + "," + prices.get(item);
		byte[] message = msg.getBytes();
		channel.basicPublish("", "sync.q", null, message);
	}
	
	private void displayCache() {
		System.out.println("");
		for (Map.Entry<String, Map<String, Integer>> cust : cache.entrySet()) {
			System.out.println(cust.getKey());
			for (Map.Entry<String, Integer> item : cust.getValue().entrySet()) {
				System.out.println("   " + item.getKey() + "(" + item.getValue() + ")");
			}
		}
	}
	
	
	public static void main(String[] args) throws Exception {
		new AMQPOrderEntryApp().execute();
	}
}
