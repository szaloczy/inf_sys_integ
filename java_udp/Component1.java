package java_udp;

import java.io.File;
import java.io.FileInputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Component1 {
        public static void main(String args[]) throws Exception {

        File imageFile = new File("java_udp/cutted.png");

        FileInputStream fis = new FileInputStream(imageFile);

        byte[] imageData = new byte[(int) imageFile.length()];
        int fileSize = (int) imageFile.length();

        byte[] buffer = new byte[1024];
        int bytesRead = 0;

        System.out.println(buffer.length);

        BufferedReader inFromUser = new BufferedReader(new InputStreamReader(System.in));
        DatagramSocket clientSocket = new DatagramSocket();
        InetAddress IPAddress = InetAddress.getByName("localhost");
            
        //byte[] sendData = new byte[1024];
        int sendData;
        byte[] receiveData = new byte[1024];
            
        //String sentence = inFromUser.readLine();
        sendData = buffer.length;

        byte[] sizeByte = String.valueOf(fileSize).getBytes();
            
        DatagramPacket sizePacket = new DatagramPacket(sizeByte, sizeByte.length, IPAddress, 8080);
        clientSocket.send(sizePacket);

        for (int i = 0; i < imageData.length; i += buffer.length) {
            bytesRead = fis.read(buffer);
            DatagramPacket filePacket = new DatagramPacket(buffer, bytesRead, IPAddress, 8080);
            clientSocket.send(filePacket);
        }
            
        DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
        clientSocket.receive(receivePacket);
        String modifiedSentence = new String(receivePacket.getData());
            
        System.out.println("transformed:" + modifiedSentence);

        clientSocket.close();
    }
}
