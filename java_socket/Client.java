import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

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

			do {
				try {
					sendMessage("list");
					files = (String[]) in.readObject();

					if(files.length > 0) {
						listFilenames(files);
						String ans = getAnswer();
						sendMessage(ans);
					} else {

						System.out.println("Files not found!");
						sendMessage("bye");
					}

					message = (String) in.readObject();
				} catch(Exception e) {
					System.err.println("data received in unknown format: " + e.getMessage());
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

	void listFilenames(String[] files) {
		for(String file : files) {
			System.out.println(file);
		}
	}

	String getAnswer() {
		String input;
		boolean ok = false;
		Scanner sc = new Scanner(System.in);
		System.out.println("What do you want to do Download (d) or Upload (u)? \n Please enter the proper letter:");
		do {
			input = sc.nextLine();
			if(input == "u" || input == "d") {
				ok = true;
			}
			ok = false;
		} while(!ok);

		return input;
	}

	public static void main(String[] args) {
		Client client = new Client();
		client.run();
	}
}
