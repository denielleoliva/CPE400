
import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.File;
import java.math.*;
import java.util.zip.Adler32;

public class Client {

    private static DataOutputStream outStream = null;
    private static DataInputStream inStream = null;

    public static void main(String[] args){
        String filePath = args[0];
        int packSize = Integer.parseInt(args[1]);

        System.out.println(filePath + " | " + packSize);


        try(Socket socket = new Socket("localhost", 5000)){

            inStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());

            int openChannelCount = 0;

            File dir = new File(filePath);

            File[] filesInDir = dir.listFiles();
            int fileCount = filesInDir.length;

            // FileWriter metaDataFile = new FileWriter("meta.txt");
            // metaDataFile.write(fileCount);
            // metaDataFile.close();

            // sendFile("meta.txt");

            outStream.writeInt(fileCount);;


            openChannelCount = concurrentSend(filePath, packSize);
            //send file verification
            // Checksum checksum = new Adler32();
            // checksum.update(buffer, 0, len);

            for(int i =0; i<openChannelCount; i++){
                inStream.close();
            }

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
        }

        fileIn.close();
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
