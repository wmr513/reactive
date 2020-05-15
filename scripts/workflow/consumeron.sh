HOME=$DEMO_HOME

CLASSPATH=$HOME/src/main/resources
CLASSPATH=$CLASSPATH:$HOME/bin
CLASSPATH=$CLASSPATH:$HOME/lib/commons-io-1.2.jar
CLASSPATH=$CLASSPATH:$HOME/lib/rabbitmq-client.jar
CLASSPATH=$CLASSPATH:$HOME/lib/commons-logging.jar
CLASSPATH=$CLASSPATH:$HOME/lib/log4j-1.2.17.jar

java -cp $CLASSPATH workflow.AMQPWorkflowConsumer2
