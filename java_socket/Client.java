import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.nio.file.Paths;
import java.util.Scanner;

public class Client {

	private static final String HOST = "localhost";
	private static final int PORT = 8080;
	private static final int BUFFER_SIZE = 4096;
	private static final String LOCAL_DIR = "java_socket/files";

	public static void main(String[] args) {
		File dir = new File(LOCAL_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}

		try (
			Socket requestSocket = new Socket(HOST, PORT);
			BufferedReader reader = new BufferedReader(new InputStreamReader(requestSocket.getInputStream()));
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(requestSocket.getOutputStream()));
			DataOutputStream dataOut = new DataOutputStream(requestSocket.getOutputStream());
			DataInputStream dataIn = new DataInputStream(requestSocket.getInputStream());
			Scanner sc = new Scanner(System.in);
		) {
			System.out.println("Connected to Server");

			while (true) {
				System.out.println("\n1 - LIST");
                System.out.println("2 - UPLOAD");
                System.out.println("3 - DOWNLOAD");
                System.out.println("4 - EXIT");
                System.out.print("Choose option: ");

				String choice = sc.nextLine();
				
				switch (choice) {
					case "1":
						writer.write("LIST\n");
						writer.flush();

						System.out.println("Files on Sever:");
						requestSocket.setSoTimeout(300);
						try {
							String line;
							while((line = reader.readLine()) != null) {
								System.out.println(" - " + line);
							}
						} catch (IOException e) {}
						requestSocket.setSoTimeout(0);
						break;
					case "2": 
						System.out.println("Enter a filename to upload: ");
						String uploadName = sc.nextLine();

						File uploadFile = new File(Paths.get(LOCAL_DIR, uploadName).toString());
						if(!uploadFile.exists()) {
							System.out.println("File does not exists");
							break;
						}

						writer.write("UPLOAD " + uploadName + "\n");
						writer.flush();

						dataOut.writeLong(uploadFile.length());

						try(FileInputStream fis = new FileInputStream(uploadFile)) {
							byte[] buffer = new byte[BUFFER_SIZE];
							int read;

							while((read = fis.read(buffer)) != -1) {
								dataOut.write(buffer, 0, read);
							}
						}
						dataOut.flush();
						break;
				
					default:
						break;
				}
			}
			
		} catch (Exception e) {
			// TODO: handle exception
		}
 	}
}