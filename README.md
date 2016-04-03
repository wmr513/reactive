# reactive
Reactive Architecture Patterns from NFJS Sessions

To run these code samples you will need Java 1.7 or higher and RabbitMQ (3.5.4). I use the rabbitmq:latest docker image from Pivotal.

Be sure to go into common.AMQPCommon.java and update the connection info for RabbitMQ:

public static Channel connect() throws Exception {	
	ConnectionFactory factory = new ConnectionFactory();	
-->	factory.setHost("192.163.98.101");
-->	factory.setPort(32768);
	Connection conn = factory.newConnection();	
	return conn.createChannel();	
}


Each reactive pattern I discuss in my session can be found in each of the directories matching the pattern name.


