
import java.io.*;
import java.net.Socket;

public class Client{

    private static DataOutputStream outStream = null;
    private static DataInputStream inStream = null;

    public static void main(String[] args){
        try(Socket socket = new Socket("localhost", 5000)){

            inStream = new DataInputStream(socket.getInputStream());
            outStream = new DataOutputStream(socket.getOutputStream());

            sendFile("one.txt");
            sendFile("two.txt");

            inStream.close();
            inStream.close();

        }catch (Exception e){
            System.out.println(e.toString());
        }
    }

    private static void sendFile(String path) throws Exception{
        int bytes = 0;

        File file = new File(path);
        FileInputStream fileIn = new FileInputStream(file);

        outStream.writeLong(file.length());

        byte[] buffer = new byte[4*1024];

        while((bytes=fileIn.read(buffer))!=-1){
            outStream.write(buffer, 0, bytes);
            outStream.flush();
        }

        fileIn.close();
    }
    
}
