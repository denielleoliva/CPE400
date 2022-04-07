

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Server{
    
    private static DataOutputStream outStream = null;
    private static DataInputStream inStream = null;

    public static void main(String[] args){

        try(ServerSocket server = new ServerSocket(5000)){
            System.out.println("listening....");
            Socket clientSocket = server.accept();

            System.out.println("connection established....");
            System.out.println(clientSocket+" connected");

            inStream = new DataInputStream(clientSocket.getInputStream());
            outStream = new DataOutputStream(clientSocket.getOutputStream());

            receiveFile("NewFile1.txt");
            receiveFile("NewFile2.txt");

            inStream.close();
            inStream.close();

            clientSocket.close();


        }catch (Exception e){
            System.out.println(e.toString());
        }

    }


    private static void receiveFile(String fileName) throws Exception{
        int bytes = 0;

        FileOutputStream fileOut = new FileOutputStream(fileName);

        long size = inStream.readLong();
        byte[] buffer = new byte[4*1024];

        while(size > 0 && (bytes = inStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1){
            fileOut.write(buffer, 0, bytes);
            size -= bytes;
        }

        fileOut.close();
    }

}
