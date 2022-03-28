using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.IO;
using System.Text;

namespace Server{

    class Program{
        static void Main(string[] args){

            TcpListener listener = new TcpListener(IPAddress.Any, 1234);

            listener.Start();

            Console.WriteLine("Listening...");


            while(true){
                
                TcpClient client = listener.AcceptTcpClient();

                Console.Write("Connection Established...");

            }

        }
    }

}