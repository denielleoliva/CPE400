

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.Adler32;
import java.util.zip.Checksum;


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

            int files = inStream.readInt();

            for(int i = 0; i<files; i++){
                String fileName = inStream.readUTF();
                receiveFile(fileName);
            }


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
        Checksum checksum = new Adler32();
        Checksum compChecksum = new Adler32();

        while(size > 0 && (bytes = inStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1){
            fileOut.write(buffer, 0, bytes);
            size -= bytes;
            receiveAdler(buffer);
        }

        fileOut.close();
    }

    private static void receiveAdler(byte[] buffer)
    {
        Checksum currentChecksum = new Adler32();

        try 
        {
            long originalChecksum = inStream.readLong();//this is the original checksum 
            currentChecksum.update(buffer);
            if(currentChecksum.getValue() == originalChecksum)
            {
                System.out.println(originalChecksum);
                System.out.println(currentChecksum.getValue());
                System.out.println("Verification complete: file integrity secure");
            }
            else
                System.out.println("Verification complete: file integrity compromised");
            }
          catch(IOException e) 
        {
            e.printStackTrace();
        }
          
        
        
    }
}
