package common;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class AMQPInitialize {

	public static void main(String[] args) throws Exception {
		Channel channel = AMQPCommon.connect();
		
		//create the durable exchanges
		channel.exchangeDeclare("flow.fx", "fanout", true);
		channel.exchangeDeclare("orders.dx", "direct", true);
		System.out.println("exchanges created.");

		//create the durable queues
		channel.queueDeclare("book.q", true, false, false, null);
		channel.queueDeclare("music.q", true, false, false, null);
		channel.queueDeclare("movie.q", true, false, false, null);
		channel.queueDeclare("order.q", true, false, false, null);
		channel.queueDeclare("flow.q", true, false, false, null);
		channel.queueDeclare("trade.eq.q", true, false, false, null);
		channel.queueDeclare("trade.1.q", true, false, false, null);
		channel.queueDeclare("trade.2.q", true, false, false, null);
		channel.queueDeclare("workflow.q", true, false, false, null);
		channel.queueDeclare("sync.q", true, false, false, null);
		System.out.println("queues created.");

		//create the bindings
		channel.queueBind("flow.q", "flow.fx", "");
		channel.queueBind("book.q", "orders.dx", "book");
		channel.queueBind("music.q", "orders.dx", "music");
		channel.queueBind("movie.q", "orders.dx", "movie");
		System.out.println("bindings created.");
		
		AMQPCommon.close(channel);
	}
}

