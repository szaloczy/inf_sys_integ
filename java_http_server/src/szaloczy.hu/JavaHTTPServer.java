import java.io.*;
import java.net.*;
import java.util.*;

public class JavaHTTPServer implements Runnable {
 
    static final File WEB_ROOT = new File("C:\\Users\\au088965\\dev\\inf_sys_integ\\java_http_server\\src\\public");
    static final String DEFAULT_FILE = "index.html";
    static final String FILE_NOT_FOUND = "404.html";
 
    private Socket connect;
 
    public JavaHTTPServer(Socket c) {
        connect = c;
    }
 
    public static void main(String[] args) {
        try {
            ServerSocket serverConnect = new ServerSocket(8080);
            System.out.println("Server started.\nListening for connections on port : 8080 ...\n");
 
            while (true) {
                JavaHTTPServer myServer = new JavaHTTPServer(serverConnect.accept());
 
                Thread thread = new Thread(myServer);
                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Server Connection error : " + e.getMessage());
        }
    }
 
    @Override
    public void run() {
        BufferedReader in = null;
        PrintWriter out = null;
        BufferedOutputStream dataOut = null;
        String fileRequested = null;
 
        try {
            in = new BufferedReader(new InputStreamReader(connect.getInputStream()));
            out = new PrintWriter(connect.getOutputStream());
            dataOut = new BufferedOutputStream(connect.getOutputStream());
 
            String input = in.readLine();

            System.out.println("input: " + input);
 
            StringTokenizer parse = new StringTokenizer(input);
            String method = parse.nextToken().toUpperCase(); // we get the HTTP method of the client
             
            // we get file requested
            fileRequested = parse.nextToken().toLowerCase();
 
            // we support only GET and HEAD methods, we check
            if (method.equals("GET") || method.equals("HEAD")) {
                // GET or HEAD method
                if (fileRequested.endsWith("/")) {
                    fileRequested += DEFAULT_FILE;
                }
                

                File file = new File(WEB_ROOT, fileRequested);
                int fileLength = (int) file.length();

                System.out.println("Content length : "+ fileLength);

                String content = getContentType(fileRequested);
 
                if (method.equals("GET")) { // GET method so we return content
                    byte[] fileData = readFileData(file, fileLength);
 
                    // send HTTP Headers
                    sendResponse(file, content, fileData, out, dataOut, "default");
                }
            }
 
        } catch (FileNotFoundException fnfe) {
            try {
                fileNotFound(out, dataOut, fileRequested);
            } catch (IOException ioe) {
                System.err.println("Error with file not found exception : " + ioe.getMessage());
            }
 
        } catch (IOException ioe) {
            System.err.println("Server error : " + ioe);
        } finally {
            try {
                in.close();
                out.close();
                dataOut.close();
                connect.close(); // we close socket connection
            } catch (Exception e) {
                System.err.println("Error closing stream : " + e.getMessage());
            }
        }
 
    }

    private void sendResponse(File file, String content, byte[] fileData, PrintWriter out, BufferedOutputStream dataOut, String type) throws IOException {
        int fileLength = (int) file.length();

        switch (type) {
            case "default":
                out.println("HTTP/1.1 200 OK");
                out.println("Server: Java HTTP Server v1.0");
                out.println("Date: " + new Date());
                out.println("Content-type: " + content);
                out.println("Content-length: " + fileLength);
                out.println(); // blank line between headers and content, very important !
                out.flush(); // flush character output stream buffer
        
                dataOut.write(fileData, 0, fileLength);
                dataOut.flush();
                break;

            case "error":
                out.println("HTTP/1.1 404 File Not Found");
                out.println("Server: Java HTTP Server v1.0");
                out.println("Date: " + new Date());
                out.println("Content-type: " + content);
                out.println("Content-length: " + fileLength);
                out.println(); // blank line between headers and content, very important !
                out.flush(); // flush character output stream buffer
        
                dataOut.write(fileData, 0, fileLength);
                dataOut.flush();
                break;

            default:
                break;
        }

        
    }
 
    private byte[] readFileData(File file, int fileLength) throws IOException {
        FileInputStream fileIn = null;
        byte[] fileData = new byte[fileLength];
 
        try {
            fileIn = new FileInputStream(file);
            fileIn.read(fileData);
        } finally {
            if (fileIn != null)
                fileIn.close();
        }
 
        return fileData;
    }
 
    // return supported MIME Types
    private String getContentType(String fileRequested) {
        if (fileRequested.endsWith(".htm") || fileRequested.endsWith(".html"))
            return "text/html";
        else
            return "text/plain";
    }
 
    private void fileNotFound(PrintWriter out, BufferedOutputStream dataOut, String fileRequested) throws IOException {
        File file = new File(WEB_ROOT, FILE_NOT_FOUND);
        int fileLength = (int) file.length();
        String content = "text/html";
        byte[] fileData = readFileData(file, fileLength);
 
       sendResponse(file, content, fileData, out, dataOut, "error");
    }
 
}