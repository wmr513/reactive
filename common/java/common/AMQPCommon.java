package common;

import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;

public class AMQPCommon {

	public static Channel connect() throws Exception {
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("127.0.0.1");
		factory.setPort(32768);
		Connection conn = factory.newConnection();
		return conn.createChannel();
	}
	
	public static void close(Channel channel) throws Exception {
		channel.close();
		channel.getConnection().close();
	}

}

