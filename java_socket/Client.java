import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;
import java.io.*;

public class Client {
	Socket requestSocket;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
	String[] files;

	Client() {}

	void run() {
		try {
			requestSocket = new Socket("localhost", 8080);

			out = new ObjectOutputStream(requestSocket.getOutputStream());
			in = new ObjectInputStream(requestSocket.getInputStream());

			message = "";
			do {
				try {
					sendMessage("list");
					files = (String[]) in.readObject();

					if(files != null && files.length > 0) {
						listFilenames(files);
						String ans = getAnswer();
						sendMessage(ans);
						switch(ans) {
							case "u": {
							uploadFile();
							break;
							}
							case "d": {
							String fname = getFileToDownload(files);
							sendMessage(fname);
							byte[] fileBytes = (byte[]) in.readObject();
							saveFile(fileBytes, fname);

							break;
							}
							default : {
							System.out.println("Invalid operation");
							}
						}
					} else {

						System.out.println("Files not found!");
						sendMessage("bye");
						message = "bye";
						break;
					}

					message = (String) in.readObject();
				} catch(Exception e) {
					System.err.println("data received in unknown format: " + e.getMessage());
					message = "bye";
				}

			}while(!"bye".equals(message));

		} catch(UnknownHostException unknownHost) {
			System.err.println("You are try to connect to an unknown host");
		} catch(IOException ioEx) {
			ioEx.printStackTrace();
		} finally {
			try {
				out.close();
				in.close();
				requestSocket.close();
			} catch(IOException ioEx) {
				ioEx.printStackTrace();
			}
		}

	}

	void sendMessage(String msg) {
		try {
			out.writeObject(msg);
			out.flush();
			System.out.println("client>" + msg);
		} catch(IOException ioEx) {
			ioEx.printStackTrace();
		}
	}

	void sendFileSize(int size) {
		try {
			out.writeObject(size);
			out.flush();
			System.out.println("client>" + size);
		} catch(IOException ioEx) {
			ioEx.printStackTrace();
		}
	}

	void sendFile(byte[] file) {
		try {
			out.writeObject(file);
			out.flush();
			System.out.println("client>" + file);
		} catch(IOException ioEx) {
			ioEx.printStackTrace();
		}
	}

	void listFilenames(String[] files) {
		for(String file : files) {
			System.out.println(file);
		}
	}

	String getAnswer() {
		String input;
		Scanner sc = new Scanner(System.in);
		System.out.println("What do you want to do Download (d) or Upload (u)? \n Please enter the proper letter:");
		do {
			input = sc.nextLine();
			if(input.equals("u") || input.equals("d")) {
				return input;
			}
			System.out.println("Invalid input! Enter 'u' or 'd':");
		} while(true);
	}

	void uploadFile() {
		String filepath = "./files/";
		try {
			FileInputStream inputStream;
			System.out.println("Enter the name of the file: ");
			Scanner sc = new Scanner(System.in);

			String file = sc.nextLine();
			if(file.length() < 0) {
				System.out.println("Filename required");
			}


			filepath += file;

			File f = new File(filepath);

			if(!f.exists()) {
				System.out.println("Given file does not exists!");
				return;
			}

			sendMessage(f.getName());

			inputStream = new FileInputStream(f);

			byte[] buffer = new byte[(int) f.length()];
			sendFileSize(buffer.length);
			inputStream.read(buffer);
			sendFile(buffer);
		} catch(Exception e) {
			System.out.println("Error: " + e.getMessage());
		} 
	}

	String getFileToDownload(String files[]) {
		boolean ok = false;
		Scanner sc = new Scanner(System.in);
		System.out.println("Enter the name of the file you want to download: ");
		String file;
		do {
			file = sc.nextLine();
			for(String f : files) {
				if(f.equals(file)) {
					ok = true;
					break;
				}
			}
			if(ok == true) {
				return file;
			}
			System.out.println("File not found! Please enter a valid filename:");

		} while(!ok);

		
		return file;
	}

	void saveFile(byte[] fileBytes, String fileName) {
		try(FileOutputStream fos = new FileOutputStream("./files/" + fileName)) {
			fos.write(fileBytes);
			System.out.println("File downloaded successfully");
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	public static void main(String[] args) {
		Client client = new Client();
		client.run();
	}
}
