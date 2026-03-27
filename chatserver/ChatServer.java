package chatserver;


import java.util.List;
import java.util.ArrayList;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public class ChatServer extends UnicastRemoteObject implements IChatServer {
    private static final long serialVersionUID = 4705993250126188735L;
    private ArrayList<IChatClient> v = new ArrayList<IChatClient>();
   
    public ChatServer() throws RemoteException {
    }
   
    public boolean login(IChatClient client) throws RemoteException {
        System.out.println(client.getName() + " got connected....");

        client.tell("client connected");
        publish(client.getName() + " connected.");
        v.add(client);
    
        for(IChatClient c : v) {
            c.broadcastNewUsers(v);
        }
        
        return true;
    }
   
    public void publish(String s) throws RemoteException {
        System.out.println(s);
        for (int i = 0; i < v.size(); i++) {
            try {
                IChatClient tmp = (IChatClient) v.get(i);
                tmp.tell(s);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
   
    public ArrayList<IChatClient> getConnected() throws RemoteException {
        return v;
    }

}
