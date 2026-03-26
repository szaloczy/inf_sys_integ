package chatserver;

import java.rmi.Naming;

public class StartServer {
    public static void main(String[] args) {
        try {
            // System.setSecurityManager(new RMISecurityManager());
            java.rmi.registry.LocateRegistry.createRegistry(1099);
            IChatServer server = new ChatServer();
            Naming.rebind("rmi://localhost:1099/chatserver", server);
            System.out.println("server ready.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
