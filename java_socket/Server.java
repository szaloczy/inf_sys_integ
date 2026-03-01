import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class Server {

	private static final int PORT = 8080;
	private static final String STORE_DIR = "store";
	private static final int BUFFER_SIZE = 4096;

	public static void main(String[] args) {
		File dir = new File(STORE_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}

		try (ServerSocket serverSocket = new ServerSocket(PORT)) {
			System.out.println("Server listenign on Port " + PORT);

			while(true) {
				Socket clientSocket = serverSocket.accept();
				System.out.println("Client connected: " + clientSocket.getInetAddress());
				new ClientHandler(clientSocket).start();
			}
		} catch(IOException e) {
			e.printStackTrace();
		}
	}

	static class ClientHandler extends Thread {
		private Socket socket;

		ClientHandler(Socket socket) {
			this.socket = socket;
		}

		@Override
		public void run() {
			try(
					BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
					BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
					DataInputStream dataIn = new DataInputStream(socket.getInputStream());
					DataOutputStream dataOut = new DataOutputStream(socket.getOutputStream());
			) {
				String command;

				while((command = reader.readLine()) != null) {
					
				}
			} catch() {

			}
		}
	}

	ServerSocket providerSocket;
	Socket connection = null;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;
	String op;

	Server() {}

	void run() {
		try {
			providerSocket = new ServerSocket(8080);

			connection = providerSocket.accept();

			out = new ObjectOutputStream(connection.getOutputStream());
			in = new ObjectInputStream(connection.getInputStream());

			do {
				try {
					message = (String) in.readObject();
					if (message.equals("list")) {
						listFileNames();
					}
					else {
						sendMessage("bye");
					}
					op = (String) in.readObject();

					if (op.equals("u")) {
						String fileName = (String) in.readObject();
						System.out.println("client> saving" + fileName);
						byte[] file = (byte[]) in.readObject();

						saveFile(file, fileName);
					} else if (op.equals("d")) {
						System.out.println("Server> Download operation requested");
						String fileName = (String) in.readObject();
						System.out.println("Client> requested file: " + fileName);
						sendFile(fileName);
					}

				} catch(ClassNotFoundException e) {
					System.err.println("Data received in unkonwn format");
				}
			}while (!message.equals("bye"));

		}catch(IOException ioException) {
			ioException.printStackTrace();
		} finally {
			try {
				in.close();
				out.close();
				providerSocket.close();
			} catch(IOException ioEx) {
				ioEx.printStackTrace();
			}
		}
	}

	void sendMessage(String msg) {
		try {
			out.writeObject(msg);
			out.flush();
			System.out.println("server>" + msg);
		}catch(IOException ioEx) {
			ioEx.printStackTrace();
		}
	}

	void listFileNames() {
		String[] fileNames;

		File f = new File("D:/Projects/university/msc_1/inf_sys_integ/java_socket/store");
		fileNames = f.list();
		if (fileNames == null) {
			fileNames = new String[0];
		}

		try{
			out.writeObject(fileNames);
			out.flush();
			
		}catch(IOException ioEx) {
			ioEx.printStackTrace();
		}
	}

	void saveFile(byte[] file, String fileName) {
		try(FileOutputStream fos = new FileOutputStream("D:/Projects/university/msc_1/inf_sys_integ/java_socket/store/" + fileName)) {
			fos.write(file);
			System.out.println("Server> File uploaded successfully");
			sendMessage("bye");
		} catch(IOException ex) {
			ex.printStackTrace();
		}
	}

	void sendFile(String fileName) {
		File file = new File("./store/" + fileName);
		if(file.exists()) {
			System.out.println("Server> File found, sending file size");
			
			try(FileInputStream fis = new FileInputStream(file)) {
				byte[] fileData = new byte[(int) file.length()];
				fis.read(fileData);
				
				out.writeObject(fileData);
				out.flush();

				System.out.println("Server> File sent successfully");
				sendMessage("bye");
			} catch(IOException ex) {
				ex.printStackTrace();
			}
		} else {
			System.out.println("Server> File not found");
			sendMessage("bye");
		}
	}



	public static void main(String[] args) {
		Server server = new Server();
		while(true) {
			if(server != null) {
				System.out.println("Server is listening on port 8080");
				server.run();
			}
		}

		try(ServerSocket serverSocket = new ServerSocket(8080)) {
			System.out.println("Server is listening on port 8080");

			while(true) {
				Socket client = serverSocket.accept();
				new ClientHandler(client).start();
			}

		} catch(IOException ex) {
			ex.printStackTrace();
		}

	}

}
