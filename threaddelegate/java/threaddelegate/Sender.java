package threaddelegate;

import java.util.Random;

import com.rabbitmq.client.Channel;
import common.AMQPCommon;

public class Sender {
	
	public static void main(String[] args) throws Exception {
		Sender app = new Sender();
		app.produceMessages();
		System.exit(0);
	}
	
	private void produceMessages() throws Exception {		
		Channel channel = AMQPCommon.connect();
		send(channel, "(1) B-123", "SELL", "AAPL");
		send(channel, "(1) Q-665", "SELL", "ATT");
		send(channel, "(2) B-123", "BUY", "IBM");
		send(channel, "(2) Q-665", "SELL", "XPH");
		send(channel, "(3) Q-665", "BUY", "GLD");
		send(channel, "(1) T-329", "SELL", "GOOG");
		send(channel, "(1) D-945", "SELL", "FB");
		send(channel, "(2) D-945", "SELL", "AAPL");
		send(channel, "(2) T-329", "BUY", "AAPL");
		send(channel, "(3) D-945", "BUY", "AMZN");
		channel.close();
	}
	
	private void send(Channel channel, String acct, String side, String symbol) throws Exception {
		long shares = ((long) ((new Random().nextDouble() * 4000) + 1));
		String text = acct + "," + side + "," + symbol +"," + shares + " SHARES";
		byte[] message = text.getBytes();
		String routingKey = "trade.eq.q";
		System.out.println("sending: " + text);
		channel.basicPublish("", routingKey, null, message);
		Thread.sleep(500);
	}
}

