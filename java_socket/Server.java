import java.net.ServerSocket;
import java.net.Socket;
import java.io.*;

public class Server {

	private static final int PORT = 8080;
	private static final int BUFFER_SIZE = 4096;
	private static final String STORE_DIR = "store";

	public static void main(String[] args) {
		File dir = new File(STORE_DIR);
		if (!dir.exists()) {
			dir.mkdir();
		}

		try (ServerSocket serverSocket = new ServerSocket(PORT)) {
			System.out.println("Server listening on Port " + PORT);

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
					if (command.equalsIgnoreCase("LIST")) {
						handleList(writer);
					} else if (command.startsWith("UPLOAD")) {
						String filename = command.split(" ")[1];
						handleUpload(filename, dataIn, writer);
					} else if (command.startsWith("DOWNLOAD")) {
						String filename = command.split(" ")[1];
						handleDownload(filename, dataOut, writer);
					} else if (command.equalsIgnoreCase("EXIT")) {
						break;
					}

					else {
						writer.write("Unknown command\n");
						writer.flush();
					}
				}
			} catch(IOException e) {
				System.out.println("Client Disconnected");
			}
		}

		private void handleList(BufferedWriter writer) throws IOException {
			File folder = new File(STORE_DIR);
			String[] files = folder.list();

			if (files == null || files.length == 0) {
				writer.write("No files found\n");
			} else {
				for (String f : files) {
					writer.write(f + "\n");
				}
			}
			writer.flush();
		}

		private void handleUpload(String filename, DataInputStream dataIn, BufferedWriter writer) throws IOException {
			if(filename.contains("..")) {
				writer.write("Invalid filename\n");
				writer.flush();
				return;
			}

			File file = new File(STORE_DIR, File.separator + filename);

			long fileSize = dataIn.readLong();

			try (FileOutputStream fos = new FileOutputStream(file)) {
				byte[] buffer = new byte[BUFFER_SIZE];
				long remaining = fileSize;
				int read;

				while(remaining > 0 && (read = dataIn.read(buffer, 0, (int)Math.min(buffer.length, remaining))) != -1) {
					fos.write(buffer, 0, read);
					remaining -= read;
				}
			}

			writer.write("File uploaded successfully\n");
			writer.flush();
		}

		private void handleDownload(String filename, DataOutputStream dataOut, BufferedWriter writer) throws IOException {
			if (filename.contains("..")) {
				writer.write("Invalid filne name\n");
				writer.flush();
				return;
			}

			File file = new File(STORE_DIR, File.separator + filename);

			if (!file.exists()) {
				writer.write("File not found\n");
				writer.flush();
				return;
			}

			writer.write("OK\n");
			writer.flush();

			dataOut.writeLong(file.length());

			try(FileInputStream fis = new FileInputStream(file)) {
				byte[] buffer = new byte[BUFFER_SIZE];
				int read;

				while((read = fis.read(buffer)) != -1) {
					dataOut.write(buffer, 0, read);
				}
			}
			dataOut.flush();
		}
	}

}