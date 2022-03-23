using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Threading.Tasks;
using System.Net;
using System.Net.Sockets;


namespace CPEFTP
{
    internal class SocketController
    {
        private static Socket ConnectSocket(string server, int port)
        {
            Socket sock = null;

            IPHostEntry hostEntry = null;

            hostEntry = Dns.GetHostEntry(server);

            foreach(IPAddress address in hostEntry.AddressList)
            {
                IPEndPoint iPEndPoint = new IPEndPoint(address, port);
                Socket tmpSock = 
                    new Socket(iPEndPoint.AddressFamily, SocketType.Stream, ProtocolType.Tcp);

                tmpSock.Connect(iPEndPoint);

                if (tmpSock.Connected)
                {
                    sock = tmpSock;
                    break;
                }
                else
                {
                    continue;
                }
            }


            return sock;
        }


    }
}
