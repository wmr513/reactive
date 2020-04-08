using System;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using System.Text;
using System.Threading;

class Consumer
{
	static void Main(string[] args)
	{
		var factory = new ConnectionFactory() { HostName = "127.0.0.1", Port = 32768 };
		using (var connection = factory.CreateConnection())
        using (var channel = connection.CreateModel())
        {
            var consumer = new EventingBasicConsumer(channel);
            var numMsgs = args.Length;
            for (int i = 0; i < numMsgs; i++)
            {
                consumer.Received += (model, msg) =>
                {
                    var body = msg.Body;
                    var message = Encoding.UTF8.GetString(body);
                    Console.WriteLine("Received: {0}", message);
                    Thread.Sleep(1000);
                    channel.BasicAck(msg.DeliveryTag, false);
				};
            }
            channel.BasicQos(0,1,true);
			channel.BasicConsume(queue: "trade.eq.q",
                                 noAck: false,
                                 consumer: consumer);
			Console.WriteLine("Waiting for messages...");
			Console.ReadLine();
			channel.Close();
		}
	}
}