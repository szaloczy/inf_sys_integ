package gyak0305;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
     
public class Server {
    private static ServerSocket providerSocket;
    private static Socket connection = null;
    private static ObjectOutputStream out;
    private static ObjectInputStream in;
    private static String message;
    private static final int BUFFER_SIZE = 4096;
     
    Server() {
    }
     
    void run() {
        try {
            // 1. szerver socket létrehozása
            providerSocket = new ServerSocket(8080);
            // 2. kapcsolódásra várakozás
            connection = providerSocket.accept();
            System.out.println("Socket listening on port: 8080");
            // 3. Input és Output streamek megadása
            out = new ObjectOutputStream(connection.getOutputStream());
            in = new ObjectInputStream(connection.getInputStream());
            // 4. socket kommunikáció
            do {
                try {
                    message = (String) in.readObject();
                    System.out.println("client>" + message);

                    if (message.equals("list")) {
                        try {
                            handleList(out);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    
                    //message = (String) in.readObject();
                    if (message.equals("u")) {
                        String filename = (String) in.readObject();
                        System.out.println("Client> " + filename);
                        try {
                            handleUpload(filename, in);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    if(message.equals("d")) {
                        String filename = (String) in.readObject();
                        System.out.println(filename);
                        try {
                            handleFileDownload(filename);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }


                    if (message.equals("bye")) {
                        sendMessage("bye");
                    } 
                } catch (ClassNotFoundException classnot) {
                    System.err.println("Data received in unknown format");
                }
            } while (!message.equals("bye"));
        } catch (IOException ioException) {
            ioException.printStackTrace();
        } finally {
            // 4: kapcsolat lezárása
            try {
                in.close();
                out.close();
                providerSocket.close();
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        }
    }
     
    void sendMessage(String msg) {
        try {
            out.writeObject(msg);
            out.flush();
            System.out.println("server>" + msg);
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    void handleList(ObjectOutputStream out) throws Exception {
        File f = new File("gyak0305/store");
        String[] files = f.list();

        if (files == null || files.length == 0)  {
            sendMessage("server> Files not found");
        } else {
            out.writeObject(files);
        }
        out.flush();

    }

    void handleUpload(String filename, ObjectInputStream in) throws Exception {
        File file = new File("gyak0305/store/"+filename);
        System.out.println("Upload requested");
        long fileSize = in.readLong();

        try (FileOutputStream fos = new FileOutputStream(file)) {
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
    void handleFileDownload(String filename) throws Exception{
        System.out.println("Download requested");
        File f = new File("gyak0305/store/"+filename);
        if(!f.exists()) {
            sendMessage("Server> File not found");
        }

        try (FileInputStream fis = new FileInputStream(f)) {
            byte[] buffer = new byte[BUFFER_SIZE];
            int read; 

            while((read = fis.read(buffer))!= -1) {
                out.write(buffer,0, read);
            }
            out.flush();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
     
    public static void main(String args[]) {
        File store_dir = new File("gyak0305/store");

        if(!store_dir.exists()) {
            store_dir.mkdir();
        }

        Server server = new Server();
        while (true) {
            server.run();
        }
    }
}