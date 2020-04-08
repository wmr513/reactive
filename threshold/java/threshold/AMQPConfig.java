package threshold;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import common.AMQPCommon;

public class AMQPConfig {

	public void execute() throws Exception {
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("config.q", true, consumer);

		//cleanup any left over messages before continuing...
		while (true) {
			if (consumer.nextDelivery(1000) == null) break;
			System.out.print(".");
		}
		
		System.out.println("");		
		System.out.println("place_trade_low:     - (-)");
		System.out.println("place_trade_current: 2000 (1000) <==");
		System.out.println("place_trade_high:    - (-)");
		System.out.println("");
		
		while (true) {
			QueueingConsumer.Delivery message = consumer.nextDelivery();
			String msg = new String(message.getBody());
			String[] values = msg.split(",");
			long low = new Long(values[0]);
			long cur = new Long(values[1]);
			long high = new Long(values[2]);
			//message in low,med,high format (duration)
			System.out.println("place_trade_low:     " + low*2 + " (" + low + ")");
			System.out.println("place_trade_current: " + cur*2 + " (" + cur + ") <==");
			System.out.println("place_trade_high:    " + high*2 + " (" + high + ")");
			System.out.println("");
		}			
	}	
	
	public static void main(String[] args) throws Exception {
		new AMQPConfig().execute();
	}
}









