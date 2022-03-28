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

			try{

				TcpClient client = new TcpClient("127.0.0.1", 1234);
				Console.WriteLine("Connection established....");

			}catch(Exception e){
				Console.Write(e.Message);
			}

			
			


		}

	}

}
