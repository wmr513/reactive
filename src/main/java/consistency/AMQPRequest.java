package consistency;

import com.rabbitmq.client.Channel;
import common.AMQPCommon;

public class AMQPRequest {

	public static void main(String[] args) throws Exception {
		Channel channel = AMQPCommon.connect();
		String cust = args[0];
		String item = args[1];
		String msg = cust + "," + item;
		byte[] message = msg.getBytes();
		System.out.println("placing order: " + msg);
		channel.basicPublish("", "order.q", null, message);
		AMQPCommon.close(channel);
	}
}
