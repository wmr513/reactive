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
			new Long(214), 
			new Long(898), 
			new Long(342), 
			new Long(905), 
			new Long(1203), 
			new Long(705), 
			new Long(262), 
			new Long(6145), 
			new Long(351), 
			new Long(1104)
	);
	
	public void execute() throws Exception {
		
		Channel channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("trade.request.q", true, consumer);

		int index = 0;
		while (true) {
			QueueingConsumer.Delivery message = consumer.nextDelivery();
			String msg = new String(message.getBody());
			System.out.println("processing trade: " + msg);
			String newMsg = "response";
			byte[] bmsg = newMsg.getBytes();
			Thread.sleep(responseTimes.get(index));
			channel.basicPublish("", "trade.response.q", null, bmsg);
			index++;
		}			
	}	
	
	public static void main(String[] args) throws Exception {
		new AMQPConsumer().execute();
	}
}









