package threshold;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.QueueingConsumer;
import common.AMQPCommon;

//timer is usually timestamp, but in this demo it is the number of messages due to 
//the long times when things get worse
public class AMQPProducer {
	
	long low = 0;
	long current = 1000;
	long high = 0;
	long timerSec = 10;
	long startTimer = System.currentTimeMillis();
	long tradeCount = 0;
	long timeout = 10; //trigger timeout after 10 trades with no adjustment
	
	List<Long> durations = new ArrayList<Long>();
	List<Long> highDurations = new ArrayList<Long>();
	double mean = 0;
	
	Channel channel = null;
	
	public void execute(String mode) throws Exception {
		channel = AMQPCommon.connect();
		QueueingConsumer consumer = new QueueingConsumer(channel);
		channel.basicConsume("trade.response.q", true, consumer);

		//cleanup any left over messages before continuing...
		while (true) {
			if (consumer.nextDelivery(1000) == null) break;
			System.out.print(".");
		}
		
		if (mode.equalsIgnoreCase("stddev")) {
			timeout = 20;
			durations.add(100l);
			durations.add(200l);
			durations.add(300l);
			durations.add(400l);
			durations.add(500l);
			durations.add(600l);
			durations.add(700l);
			durations.add(800l);
			durations.add(900l);
			durations.add(1000l);
		}
		
		while (true) {
			System.out.println("");
			long start = System.currentTimeMillis();
			long shares = ((long) ((new Random().nextDouble() * 4000) + 1));
			String text = "BUY,AAPL," + shares;
			byte[] message = text.getBytes();
			System.out.println("sending trade order: " + text);
			channel.basicPublish("", "trade.request.q", null, message);
			consumer.nextDelivery();
			tradeCount++;
			long end = System.currentTimeMillis();
			long duration = end - start;
			if (duration > (current*2)) {
				System.out.println("request timed out...");
			} else {
				System.out.println("trade confirmation received in " + duration + "ms");

				if (mode.equalsIgnoreCase("simple")) {
					adjustSimple(duration);
				} else if (mode.equalsIgnoreCase("bracket")) {
					adjustBracket(duration, false);
				} else if (mode.equalsIgnoreCase("stddev")) {
					adjustBracket(duration, true);
				}
			}
			
			Thread.sleep(1000);
		}
	}
	
	private void adjustSimple(long duration) throws Exception {
		if (duration > current) {
			current = duration;
			String msg = low + "," + current + "," + high;
			byte[] configMsg = msg.getBytes();
			System.out.println("changing timeout: " + current);
			channel.basicPublish("", "config.q", null, configMsg);
		}
	}

	private void adjustBracket(long duration, boolean stdMode) throws Exception {
		//check for timeout
		//long check = System.currentTimeMillis();
		//if ((check - startTimer) > (timerSec*1000)) {
		
		if (stdMode) {
			durations.add(duration);
			double std = calcStdDev();
			double numDeviations = Math.abs((duration - mean)/std);
			if (numDeviations > 3.0) {
				System.out.println("STDDEV > 3, REJECTING DURATION");
				return;
			}
		}
		
		if (tradeCount > timeout) {
			high = current;
			current = low;
			low = 0; 
			String msg = low + "," + current + "," + high;
			byte[] configMsg = msg.getBytes();
			System.out.println("==> SHIFT-RIGHT (timeout/better)");
			channel.basicPublish("", "config.q", null, configMsg);
			startTimer = System.currentTimeMillis();
			tradeCount = 0;
			return;
		}

		
		//check low value (between low and current)
		if (duration < current && duration > low) {
			low = duration;
			String msg = low + "," + current + "," + high;
			byte[] configMsg = msg.getBytes();
			System.out.println("==> SET LOW: " + low);
			channel.basicPublish("", "config.q", null, configMsg);
			startTimer = System.currentTimeMillis();
			tradeCount = 0;
			return;
		}

		//check max (higher than current or high value)
		if (duration > current) {
			if (high == 0) {
				high = duration;
				String msg = low + "," + current + "," + high;
				byte[] configMsg = msg.getBytes();
				System.out.println("==> SET HIGH: " + high);
				channel.basicPublish("", "config.q", null, configMsg);
				startTimer = System.currentTimeMillis();
				tradeCount = 0;
			} else {
				if (duration > high) {
					if (stdMode) {
						highDurations.add(duration);
						System.out.println("==> CONSIDERING HIGH DURATION: " + duration);
						if (highDurations.size() > 5) {
							Double mean = highDurations.stream().mapToLong(d->d).average().orElse(0.0);
							duration = mean.longValue();
							System.out.println("==> ARRAY FULL, MEAN = " + duration);
							highDurations.clear();
						} else {
							return;
						}
					}
					low = current;
					current = high;
					high = duration; 
					String msg = low + "," + current + "," + high;
					byte[] configMsg = msg.getBytes();
					System.out.println("==> SHIFT-LEFT (worse): " + duration);
					channel.basicPublish("", "config.q", null, configMsg);
					startTimer = System.currentTimeMillis();
					tradeCount = 0;
				}
			}
		}
	}

	private double calcStdDev() {
		mean = durations.stream().mapToLong(d->d).average().orElse(0.0);
		double squareDiffSum = 0;
		for (long dur : durations) {
			squareDiffSum += Math.pow((dur-mean),2);
		}
		return Math.sqrt((squareDiffSum/(durations.size()-1.0)));
	}
	
	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			System.out.println("specify mode (simple, bracket, stddev)");
		} else {
			new AMQPProducer().execute(args[0]);		
		}
	}
}





