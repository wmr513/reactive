package producerflow;

import com.rabbitmq.client.Channel;
import common.AMQPCommon;

public class AMQPFlowMonitor {
	
	public void run() throws Exception {
		Channel channel = AMQPCommon.connect();
		long threshold = 10;
		boolean controlFlow = false;
		while (true) {
			long queueDepth = channel.messageCount("trade.eq.q");
			if (queueDepth > threshold && !controlFlow) {
				controlFlow = enableControlFlow(channel);
			} else if (queueDepth <= (threshold/2) && controlFlow) {
				controlFlow = disableControlFlow(channel);
			}
			Thread.sleep(3000);
		}
	}
	
	private boolean enableControlFlow(Channel channel) throws Exception {
		System.out.println("Enabling producer control flow...");
		byte[] msg = String.valueOf(true).getBytes();
		channel.basicPublish("flow.fx", "", null, msg);
		return true;
	}
	
	private boolean disableControlFlow(Channel channel) throws Exception {
		System.out.println("Disabling producer control flow...");
		byte[] msg = String.valueOf(false).getBytes();
		channel.basicPublish("flow.fx", "", null, msg);
		return false;
	}

	public static void main(String[] args) throws Exception {
		AMQPFlowMonitor app = new AMQPFlowMonitor();
		app.run();
	}
}





