package chatserver;

import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
   
public class ChatClient extends UnicastRemoteObject implements IChatClient {
    private String name;
    private ChatUI ui;
   
    public ChatClient(String n) throws RemoteException {
        name = n;
    }
   
    public void tell(String st) throws RemoteException {
        System.out.println(st);
        ui.writeMsg(st);
    }
   
    public String getName() throws RemoteException {
        return name;
    }
   
    public void setGUI(ChatUI t) {
        ui = t;
    }

    @Override
    public void updateUsers(ArrayList<IChatClient> users) throws RemoteException {
        ui.updateUsers(users);
    }
}