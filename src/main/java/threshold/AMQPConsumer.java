package threshold;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import common.AMQPCommon;

public class AMQPConsumer {

	private List<Long> responseTimes = Arrays.asList(
			new Long(200), 
			new Long(800), 
			new Long(300), 
			new Long(900), 
			new Long(1200), 
			new Long(700), 
			new Long(200), 
			new Long(300), 
			new Long(1100), 
			new Long(1600) 
	);
	
	public void execute() throws Exception {
		
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("trade.request.q", true, consumer);

		int index = 0;
		while (true) {
			QueueingConsumer.Delivery message = consumer.nextDelivery();
			String msg = new String(message.getBody());
			System.out.println("trade received: " + msg);
			String newMsg = "response";
			byte[] bmsg = newMsg.getBytes();
//			long sleep = ((long) ((new Random().nextDouble() * 2000) + 1));
//			Thread.sleep(sleep);
			Thread.sleep(responseTimes.get(index));
			channel.basicPublish("", "trade.response.q", null, bmsg);
			index++;
		}			
	}	
	
	public static void main(String[] args) throws Exception {
		new AMQPConsumer().execute();
	}
}









