package java_udp;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class Component2 {
        public static void main(String args[]) throws Exception {
            
        DatagramSocket serverSocket = new DatagramSocket(8080);
            
        byte[] bytesReceived = new byte[1024];
        byte[] bytesSent = new byte[1024];
            
            
        DatagramPacket receivePacket = new DatagramPacket(bytesReceived, bytesReceived.length);
        //waiting
        serverSocket.receive(receivePacket);
                
        String text = new String(receivePacket.getData());
                
        System.out.println("kaptam: " + text);
                
        InetAddress IPAddress = receivePacket.getAddress();
        int port = receivePacket.getPort();
                
        String uppercasedText = text.toUpperCase();
        bytesSent = uppercasedText.getBytes();
                
        // send back
        DatagramPacket sendPacket = new DatagramPacket(bytesSent, bytesSent.length, IPAddress, port);
        serverSocket.send(sendPacket);
        serverSocket.close();
            
    }
}
