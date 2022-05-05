// Java import statements
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.math.*;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

// Client public class declaration
// Contains all the code for the client and can be compiled alongside the server to run a concurrent file transfer
public class Client {

    // File output and input variable declaration
    private static DataOutputStream outStream = null;
    private static DataInputStream inStream = null;

    // Client main function
    public static void main(String[] args){

        // Variable declaration
        String filePath = "";
        int packSize = 0;

        // If file path is not specified, print error message
        if(args.length == 0){
            System.out.println("Error: source folder path not provided");
            System.exit(0);            
        }
        // If concurrency file is not specified, set packSize to 1
        if(args.length == 1){
            filePath = args[0];
            packSize = 1;
        }        
        // If file path and concurrency file are specified, set them to the user specified value
        // Ensures that the number of concurrency file transfers is between 0 and 10
        else if(args.length == 2 && Integer.parseInt(args[1]) < 11 && Integer.parseInt(args[1]) > 0){
            filePath = args[0];
            packSize = Integer.parseInt(args[1]);
        }
        // Prints error message if the arguments exceed 3
        else if(args.length >= 3){
            System.out.println("Error: number of arguments exceed 2");
            System.exit(0);            
        }
        // Prints error message if the number concurrency file tranfers is not within acceptable values
        else{
            System.out.println("Error: concurrency file size transfers must be between 1 and 10");
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

    // sendFile function
    // Opens up the input and output files to send data to the server
    private static void sendFile(String path) throws Exception{
        
        // Variable declaration
        int bytes = 0;

        // File input and output variable declaration
        File file = new File(path);
        FileInputStream fileIn = new FileInputStream(file);

        outStream.writeUTF(file.getName());
        outStream.writeLong(file.length());

        // Sets buffer
        byte[] buffer = new byte[4*1024];

        // While the buffer is not hit, transfer file contents
        while((bytes=fileIn.read(buffer))!=-1){
            outStream.write(buffer, 0, bytes);
            outStream.flush();
            sendAdler32(buffer);
        }

        // Closes file
        fileIn.close();
    }

    // sendAdler32 function
    // Creates a checksum for the file
    private static void sendAdler32(byte[] buffer)
    {
        // Checksum variable declaration
        Checksum checksum = new Adler32();
        checksum.update(buffer, 0, buffer.length);
        
        // Grabs a checksum for the file
        try{
            outStream.writeLong(checksum.getValue());
        }
        // Error catch to prevent bugs if response was not expected
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }

    // ConcurrentSend function
    // Opens the file directory that we are sending from to calculate how many files we are sending within a packet and how many connections we will need
    // It will then concurrently send files in packets until all of the files are transferred
    public static int concurrentSend(String path, int sendCount)throws Exception{
        
        // Variable declaration
        int counter = 0;
        int connections = 0;
        
        // File directory variable declaration
        File dir = new File(path);
        
        File[] filesInDir = dir.listFiles();
        int fileCount = filesInDir.length;

        // Sets temp with the modulo of files in directory and concurrency file transfer rate for manipulation and comparison
        int temp = filesInDir.length%sendCount;

        // Sets temp2 with the files in directory divided by the concurrency file transfer rate for manipulation and comparison
        int temp2 = filesInDir.length/sendCount;

        // Sets temp3 with the files in direcotry divided by the concurrency file transfer rate for manipulation and comparison
        double temp3 = filesInDir.length/(Double.valueOf(sendCount));
        
        // Calculate how many connections we should use for the file transfers
        if (temp != 0 && temp2 == 0)
        {
            connections = filesInDir.length;
        }
        else {
            connections = sendCount;
        }

        // Transfers files in packets until all the files are transferred
        if (temp != 0 && temp2 != temp3)
        {
            for(int i = 0; i <= temp2; i++)
            {
                if(filesInDir != null && counter < fileCount){
                    System.out.println("Sending packet number: " + (i + 1));
                    for(int j = 0; j < connections; j++)
                    {
                        if(counter<fileCount){
                            System.out.println("Sending file number: " + (counter + 1));
                            sendFile(filesInDir[counter].getAbsolutePath());
                        }else{
                            break;
                        }
                        counter++;
                    }
                }
                // If there are no files detected, print error message
                else{
                System.out.println("Directory emptied");
                System.exit(0);
                }
            }
        }
        else
        {
            for(int i = 0; i < temp2; i++)
            {
                if(filesInDir != null && counter < fileCount){
                    System.out.println("Sending packet number: " + (i + 1));
                    for(int j = 0; j < connections; j++)
                    {
                        if(counter<fileCount){
                            System.out.println("Sending file number: " + (counter + 1));
                            sendFile(filesInDir[counter].getAbsolutePath());
                        }else{
                            break;
                        }
                        counter++;
                    }
                }
                // If there are no files detected, print error message
                else{
                System.out.println("Directory emptied");
                System.exit(0);
                }
            }            
        }


        // Return the number of connections used for the file transfer
        return connections;
    }
    
}
