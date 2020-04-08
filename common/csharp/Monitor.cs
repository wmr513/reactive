using System;
using RabbitMQ.Client;
using System.Threading;

class Monitor
{
    #pragma warning disable RECS0135
    static void Main(string[] args)
    {
		var factory = new ConnectionFactory() { HostName = "127.0.0.1", Port = 32768 };
		using (var connection = factory.CreateConnection())
		using (var channel = connection.CreateModel())
		{
			while (true)
			{
                long consumers = channel.ConsumerCount("trade.eq.q");
				long queueDepth = channel.MessageCount("trade.eq.q");
				Console.WriteLine("consumers: " + consumers + ", pending msgs:" + queueDepth);
				Thread.Sleep(1000);
			}
		}
	}
}
