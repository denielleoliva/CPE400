// Java import statements
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.zip.Adler32;
import java.util.zip.Checksum;

// Server public class declaration
// Contains all the code for the server and can be compiled alongside the client to run a concurrent file transfer
public class Server{
    
    // File output and input variable declaration
    private static DataOutputStream outStream = null;
    private static DataInputStream inStream = null;

    // Client main function
    public static void main(String[] args){

        // 
        try(ServerSocket server = new ServerSocket(5000)){
            
            // Prints message that informs the user that the program is listening for the client
            System.out.println("listening....");
            Socket clientSocket = server.accept();

            // Informs the user that a connection was established with a client
            System.out.println("connection established....");
            System.out.println(clientSocket+" connected");

            // File input and output variable declaration based on the client
            inStream = new DataInputStream(clientSocket.getInputStream());
            outStream = new DataOutputStream(clientSocket.getOutputStream());

            // Reads in the number of files
            int files = inStream.readInt();

            // Reads in file names, and will run the receiveFile function which outputs them to the server
            for(int i = 0; i<files; i++){
                String fileName = inStream.readUTF();
                receiveFile(fileName);
            }

            // Closes files
            inStream.close();
            inStream.close();

            // Closes socket
            clientSocket.close();


        // Error catch to prevent bugs if response was not expected       
        }catch (Exception e){
            System.out.println(e.toString());
        }

    }


    // receiveFile function
    // Opens up the input and output files to receive data from the client
    private static void receiveFile(String fileName) throws Exception{
        
        // Variable declaration
        int bytes = 0;

        // File output variable declaration
        FileOutputStream fileOut = new FileOutputStream(fileName);

        long size = inStream.readLong();
        byte[] buffer = new byte[4*1024];
        
        // Checksum variable declaration
        Checksum checksum = new Adler32();
        Checksum compChecksum = new Adler32();

        // While the buffer and file size restrictions are not broken, output the files into the server
        while(size > 0 && (bytes = inStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1){
            fileOut.write(buffer, 0, bytes);
            size -= bytes;
            receiveAdler(buffer);
        }

        // Closes file
        fileOut.close();
    }

    // receiveAdler function
    // Checks the checksum to see if it matches the original checksum
    // If the checksums match, then the verification is complete
    // If the checksums do not match, then it will print an error message
    private static void receiveAdler(byte[] buffer)
    {
        // Checksum variable declaration
        Checksum currentChecksum = new Adler32();

        // Checks if the checksum provided in the file matches the original checksum
        try 
        {
            long originalChecksum = inStream.readLong(); //this is the original checksum 
            currentChecksum.update(buffer, 0, buffer.length);
            
            // If checksums match, print that the file's integrity is secure
            if(currentChecksum.getValue() == originalChecksum)
            {
                System.out.println(originalChecksum);
                System.out.println(currentChecksum.getValue());
                System.out.println("Verification complete: file integrity secure");
            }
            // If checksums are different, print that the file's integrity is compromised
            else
                System.out.println("Verification complete: file integrity compromised");
            }
          
            // Error catch to prevent bugs if response was not expected
            catch(IOException e) 
        {
            e.printStackTrace();
        }
    }
}
