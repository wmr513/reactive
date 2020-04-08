using System;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using System.Text;
using System.Threading;

class Producer
{
	static void Main(string[] args)
	{
		var factory = new ConnectionFactory() { HostName = "127.0.0.1", Port = 32768 };
		using (var connection = factory.CreateConnection())
		using (var channel = connection.CreateModel())
		{
			var consumer = new EventingBasicConsumer(channel);
			int numMsgs = Convert.ToInt32(args[0]);
			var random = new Random();
			for (int i = 0; i < numMsgs; i++)
			{
                var shares = (long)(random.NextDouble() * 4000 + 1); 
				String message = "BUY AAPL " + shares + " SHARES";				
				var body = Encoding.UTF8.GetBytes(message);

				channel.BasicPublish(exchange: "",
									 routingKey: "trade.eq.q",
									 basicProperties: null,
									 body: body);
				Console.WriteLine("Sending: {0}", message);
			}
            channel.Close();
		}
	}
}