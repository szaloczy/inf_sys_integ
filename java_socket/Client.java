import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class Client {
	Socket requestSocket;
	ObjectOutputStream out;
	ObjectInputStream in;
	String message;

	Client() {}

	void run() {
		try {
			requestSocket = new Socket("localhost", 8080);

			out = new ObjectOutputStream(requestSocket.getOutputStream());
			in = new ObjectInputStream(requestSocket.getInputStream());

			do {
				try {
					sendMessage("Hello Server");
					sendMessage("bye");
					message = (String) in.readObject();
				} catch(Exception e) {
					System.err.println("data received in unknown format");
				}
			}while(!message.equals("bye"));
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

	public static void main(String[] args) {
		Client client = new Client();
		client.run();
	}
}
