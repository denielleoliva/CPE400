// Java import statements
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.math.*;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

public class Client {

    // File output and input variable declaration
    private static DataOutputStream outStream = null;
    private static DataInputStream inStream = null;

    // Client main function
    public static void main(String[] args){

        // Variable declaration
        String filePath = args[0];
        int packSize = 0;

        // Ensures that the number of concurrency file transfers is between 0 and 10
        if (Integer.parseInt(args[1]) < 11 && Integer.parseInt(args[1]) > 0){
            packSize = Integer.parseInt(args[1]);
        }
        // If concurrency file is not specified, set packSize to 1
        else if(args.length == 1){
            packSize = 1;
        }
        // Prints error message if the number concurrency file tranfers is not within acceptable values
        else{
            System.out.println("Error: Concurrency file transfers must be between 0 and 10");
            System.exit(0);
        }


        // Prints out file path as well as concurrency file transfer size
        System.out.println(filePath + " | " + packSize);

        // Try localhost port for file transfer
        try(Socket socket = new Socket("localhost", 5000)){

            // Variable and file declarations
            inStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());

            File dir = new File(filePath);
            File[] filesInDir = dir.listFiles();
            int fileCount = filesInDir.length;

            // FileWriter metaDataFile = new FileWriter("meta.txt");
            // metaDataFile.write(fileCount);
            // metaDataFile.close();

            // sendFile("meta.txt");

            // ***There was an extra semicolon here, deleted it bc i think it was a typo***
            outStream.writeInt(fileCount);
            
            // Sets how many channels we have as well as run the file transfer through concurrentSend
            int openChannelCount = concurrentSend(filePath, packSize);
            
            // Closes the channels
            for(int i =0; i<openChannelCount; i++){
                inStream.close();
            }

        // Exception error message
        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    private static void sendFile(String path) throws Exception{
        int bytes = 0;

        File file = new File(path);
        FileInputStream fileIn = new FileInputStream(file);

        outStream.writeUTF(file.getName());
        outStream.writeLong(file.length());


        byte[] buffer = new byte[4*1024];

        while((bytes=fileIn.read(buffer))!=-1){
            outStream.write(buffer, 0, bytes);
            outStream.flush();
            sendAdler32(buffer);
        }

        fileIn.close();
    }

    private static void sendAdler32(byte[] buffer)
    {
        Checksum checksum = new Adler32();
        checksum.update(buffer);
        try{
            outStream.writeLong(checksum.getValue());
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    public static int concurrentSend(String path, int sendCount)throws Exception{
        File dir = new File(path);

        File[] filesInDir = dir.listFiles();
        int fileCount = filesInDir.length;

        int temp = filesInDir.length%sendCount;
        if (temp != 0)
        {
            temp = (fileCount / sendCount) + 1;
        }
        else {
            temp = fileCount/sendCount;
        }
        int connections = temp;

        int counter = 0;

        if(filesInDir != null){
            for(int i = 0; i<connections; i++)
            {
                System.out.println("Sending packet size: " + connections);
                for(int j = 0; j < sendCount; j++)
                {
                    System.out.println("Sending file number: " + j);
                    if(counter<fileCount){
                        sendFile(filesInDir[counter].getAbsolutePath());
                    }else{
                        break;
                    }
                    counter++;
                }
            }
        }else{
            System.out.println("Directory empty....");
        }

        return connections;
    }
    
}
