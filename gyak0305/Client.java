package gyak0305;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.file.Paths;
import java.util.Scanner;
     
public class Client {
    private static Socket requestSocket;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static String message;
    private static final Scanner sc = new Scanner(System.in);
    private static final int BUFFER_SIZE = 4096;
     
    Client() {
    }
     
    void run() {
        try {
            // 1. socket kapcsolat létrehozása
            requestSocket = new Socket("localhost", 8080);
            // 2. Input and Output streamek
            out = new ObjectOutputStream(requestSocket.getOutputStream());
            in = new ObjectInputStream(requestSocket.getInputStream());
            // 3: Kommunikáció
            do {
                try {
                    sendMessage("list");

                    String[] files = (String[]) in.readObject();
                    handleFiles(files);

                    String choice = sc.nextLine();

                    switch (choice) {
                        case "u":
                            sendMessage(choice);
                            handleFileUpload();
                            break;

                        case "d":
                            sendMessage(choice);
                            handleFileDownload(in);
                            break;
                    
                        default: 
                            break;
                    }

                    sendMessage("bye");
                    message = (String) in.readObject();
                } catch (Exception e) {
                    System.err.println("data received in unknown format");
                }
            } while (!message.equals("bye"));
        } catch (UnknownHostException unknownHost) {
            System.err.println("You are trying to connect to an unknown host!");
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            // 4: Kapcsolat zárása
            try {
                in.close();
                out.close();
                requestSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
     
    void sendMessage(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
            System.out.println("client>" + msg);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    void handleFiles(String[] files) {
        System.out.println("Files:");
        for(String f : files) {
            System.out.println("-" +f + "\n");
        }
    }

    void handleFileUpload() {
        System.out.print("Enter a file name: \n");
        String filename = sc.nextLine();

        File f = new File("gyak0305/files/"+filename);
        if(!f.exists()) {
            System.out.println("Client> File no found");
            return;
        }

        sendMessage(filename);


        try (FileInputStream fis = new FileInputStream(f)){
            byte[] buffer = new byte[BUFFER_SIZE];
            int read;

            while((read = fis.read(buffer)) != -1) {
                out.write(buffer, 0, read);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
        
    }

    void handleFileDownload(ObjectInputStream in) throws Exception{
        System.out.print("Enter a filename:\n");
        String filename = sc.nextLine();

        sendMessage(filename);

        File f = new File("gyak0305/files/"+filename);

        long fileSize = in.readLong();

        try (FileOutputStream fos = new FileOutputStream(f)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            long remaining = fileSize;
            int read;

            while(remaining > 0 && (read = in.read(buffer, 0, (int)Math.min(buffer.length, remaining))) != -1) {
                fos.write(buffer, 0, read);
                remaining -= read;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
     
    public static void main(String args[]) {
        File files_dir = new File("gyak0305/files");
        if(!files_dir.exists()) {
            files_dir.mkdir();
        }
        Client client = new Client();
        client.run();
    }
}