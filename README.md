# reactive
Reactive Architecture Patterns from NFJS Sessions

To run these code samples you will need Java 1.7 or higher and RabbitMQ (3.5.4). I use the rabbitmq:latest docker image from Pivotal.

Be sure to go into common.AMQPCommon.java and update the connection info for RabbitMQ: (you can get this info from the RabbitMQ logs or doing a "docker ps" if you are using the docker image)

public static Channel connect() throws Exception {	
	ConnectionFactory factory = new ConnectionFactory();	

-->	factory.setHost("192.163.98.101");

-->	factory.setPort(32768);

	Connection conn = factory.newConnection();	
	return conn.createChannel();	
}

You will also need to be sure and run the AMQPInitialize class to setup all of the exchanges, queues, and bindings used by these examples.

If you want to run the scripts, you will need to set an environment variable REACT_HOME that points to the directory containing the src and lib directory where the class files and libraries are located. 

Each reactive pattern I discuss in my session can be found in each of the directories matching the pattern name.


