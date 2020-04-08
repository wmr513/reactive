HOME=$REACT_HOME

CLASSPATH=$HOME/src/main/resources
CLASSPATH=$CLASSPATH:$HOME/bin
CLASSPATH=$CLASSPATH:$HOME/lib/commons-io-1.2.jar
CLASSPATH=$CLASSPATH:$HOME/lib/rabbitmq-client.jar

java -cp $CLASSPATH monitor.AMQPMonitor
