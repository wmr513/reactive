using System;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using System.Text;

namespace ConfigServer
{
    class ConfigServer
    {
        static void Main(string[] args)
        {
			var factory = new ConnectionFactory() { HostName = "127.0.0.1", Port = 32768 };
			Console.WriteLine("place_trade, 2000");
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
                        Console.WriteLine("UPDATED: place_trade, {0}", message);
						Console.WriteLine("notifying other producers...");
					};
				}
				channel.BasicConsume(queue: "config.q",
									 noAck: true,
									 consumer: consumer);
				Console.WriteLine("Waiting for updates...");
				Console.ReadLine();
				channel.Close();
			}
        }
    }
}
