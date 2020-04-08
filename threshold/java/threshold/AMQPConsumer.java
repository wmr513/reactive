package threshold;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;

import common.AMQPCommon;

public class AMQPConsumer {

	//actual response times
	int lower = 0;
	int upper = 0;
	
	public void execute(String mode) throws Exception {
		
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("trade.request.q", true, consumer);
		
		if (mode.equalsIgnoreCase("stable")) {lower = 300; upper = 1200;}
		if (mode.equalsIgnoreCase("better")) {lower = 300; upper = 800;}
		if (mode.equalsIgnoreCase("worse")) {lower = 800; upper = 1900;}
		if (mode.equalsIgnoreCase("erratic")) {lower = 200; upper = 5000;}

		while (true) {
			QueueingConsumer.Delivery message = consumer.nextDelivery();
			String msg = new String(message.getBody());
			System.out.println("trade order received: " + msg);
			int response = lower + (int) (Math.random() * (upper-lower));
			System.out.println("trade placed, duration = " + response);
			String newMsg = "response";
			byte[] bmsg = newMsg.getBytes();
			Thread.sleep(response);
			channel.basicPublish("", "trade.response.q", null, bmsg);
		}
	}	
	
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("specify mode (worse, better, stable, erratic)");
		} else {
			new AMQPConsumer().execute(args[0]);
		}
	}
}









