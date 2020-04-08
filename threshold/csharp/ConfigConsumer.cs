using System;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using System.Text;
using System.Threading;
using System.Collections.Generic;

namespace ConfigConsumer
{
    class ConfigConsumer
    {
        static void Main(string[] args)
        {
			var factory = new ConnectionFactory() { HostName = "127.0.0.1", Port = 32768 };
            var index = 0;
			using (var connection = factory.CreateConnection())
			using (var channel = connection.CreateModel())
			{
				var consumer = new EventingBasicConsumer(channel);
				consumer.Received += (model, msg) =>
				{
					var body = msg.Body;
					var message = Encoding.UTF8.GetString(body);
					Console.WriteLine("Processing Trade: {0}", message);
                    index++;
				};
				channel.BasicConsume(queue: "trade.request.q",
                                     noAck: true,
									 consumer: consumer);
				Console.WriteLine("Waiting for messages...");
				Console.ReadLine();
				channel.Close();
			}
		}

    }
}
