import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.io.File;

public class Server {

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
					System.out.println("client> " + message);

					if (message.equals("list")) {
						listFileNames();
					}
					else {
						sendMessage("bye");
					}
					op = (String) in.readObject();

					if (op.equals("u")) {
						String fileName = (String) in.readObject();
						System.out.println("client>" + fileName);
						int size = (int) in.readObject();
						System.out.println("client>" + size);

						byte[] file = (byte[]) in.readObject();

						saveFile(size, file, fileName);
					} else if (op.equals("d")) {
						System.out.println("Download operation requested");
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

		try{
			out.writeObject(fileNames);
			out.flush();

		}catch(IOException ioEx) {
			ioEx.printStackTrace();
		}
	}

	void saveFile(int fileSize, byte[] file, String fileName) {
		try(OutputStream out = new OutputStream) {
			//TODO
		} catch(Exception e) {
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

	}

}
