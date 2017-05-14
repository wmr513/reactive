using System;
using RabbitMQ.Client;
using RabbitMQ.Client.Events;
using System.Text;
using System.Threading;
using System.Collections.Generic;
using System.Diagnostics;

namespace ConfigProducer
{
    class ConfigProducer
    {
        static void Main(string[] args)
        {
            //simulate response times from consumer...
			List<int> responseTimes = new List<int>();
			responseTimes.Add(214);
			responseTimes.Add(898);
			responseTimes.Add(342);
			responseTimes.Add(905);
			responseTimes.Add(1203);
			responseTimes.Add(705);
			responseTimes.Add(262);
			responseTimes.Add(6145);
			responseTimes.Add(351);
			responseTimes.Add(1104);

			long threshold = 2000; //read from config server...
			var factory = new ConnectionFactory() { HostName = "127.0.0.1", Port = 32768 };
			var random = new Random();
			using (var connection = factory.CreateConnection())
			using (var channel = connection.CreateModel())
			{
				var consumer = new EventingBasicConsumer(channel);
				var numMsgs = args.Length;
				for (int i = 0; i < 10; i++)
				{
					var shares = (long)(random.NextDouble() * 4000 + 1);
					String message = "BUY AAPL " + shares + " SHARES";
					var newMsg = Encoding.UTF8.GetBytes(message);
					Console.WriteLine("Sending Trade: {0}", message);
					channel.BasicPublish("", "trade.request.q", null, newMsg);

                    var timer = Stopwatch.StartNew();
					var start = timer.ElapsedMilliseconds;

					BasicGetResult result = channel.BasicGet("trade.response.q", true);
					var end = timer.ElapsedMilliseconds;
                    var duration = responseTimes[i];
					timer.Stop();
                    Thread.Sleep(responseTimes[i]);
					Console.WriteLine("trade confirmation received in " + duration + " ms");
					if ((duration * 2) > (threshold * 1.3))
					{
						Console.WriteLine("duration exceeds 30% of threshold");
						Console.WriteLine("possible outlier detected");
						Console.WriteLine("timer started");
					}
					else if ((duration * 2) > threshold)
					{
						threshold = duration * 2;
						String updateMsg = "" + threshold;
						var newConfigMsg = Encoding.UTF8.GetBytes(updateMsg);
						Console.WriteLine("updating threshold: " + updateMsg);
						channel.BasicPublish("", "config.q", null, newConfigMsg);
					}
					Console.WriteLine("");
					Console.ReadLine();
				}
				Console.WriteLine("");
				Console.ReadLine();
				channel.Close();
			}
		}
    }
}
