using System;
using System.Collections.Generic;
using System.Net;
using System.Net.Sockets;
using System.IO;
using System.Text;
using System.Security.Cryptography;

namespace Server{

    class Program{
        static void Main(string[] args){

            TcpListener listener = new TcpListener(IPAddress.Any, 1234);

            listener.Start();

            Console.WriteLine("Listening...");


            while(true){
                
                TcpClient client = listener.AcceptTcpClient();

                Console.Write("Connection Established...");

                StreamReader reader = new StreamReader(client.GetStream());

                string fileSize = reader.ReadLine();

                string fileName = reader.ReadLine();

                int len = Convert.ToInt32(fileSize);
                byte[] buffer = new byte[len];
                int received = 0;
                int read = 0;
                int size = 1024;
                int remaining = 0;

                while(received<len){
                    remaining = len - received;
                    if(remaining<size){
                        size = remaining;
                    }


                    read = client.GetStream().Read(buffer, received, size);
                    received+=read;

                    
                    
                }

                using(FileStream fileStream = new FileStream(Path.GetFileName(fileName), FileMode.Create)){
                    fileStream.Write(buffer, 0, buffer.Length);
                    fileStream.Flush();
                    fileStream.Close();
                }


                Console.WriteLine(Environment.CurrentDirectory);

            }

        }
    }

}