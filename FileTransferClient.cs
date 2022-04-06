using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.IO;
using System.Text;

namespace FileTransferClient{

	class Program{

		static void Main(string[] args){

			Console.WriteLine(args[0]);
			Console.WriteLine(args[1]);


			int packetSize = 1;
			byte[] bStream;

			if(Int32.Parse(args[1])>1){
				packetSize = Int32.Parse(args[1]);
			}

			try{

				TcpClient client = new TcpClient("127.0.0.1", 5050);
				Console.WriteLine("Connection established....");

				StreamWriter sw = new StreamWriter(client.GetStream());

				string[] filePaths = Directory.GetFiles(args[0]);

				foreach(var fileName in filePaths){
					string file = fileName.ToString();

					bStream = File.ReadAllBytes(file);

					sw.WriteLine(bStream.Length.ToString());
					sw.Flush();

					sw.WriteLine(file);
					sw.Flush();
					
					Console.WriteLine("Sending file...");
					client.Client.SendFile(file);

				}

			}catch(Exception e){
				Console.Write(e.Message);
			}

			
			


		}

	}

}
