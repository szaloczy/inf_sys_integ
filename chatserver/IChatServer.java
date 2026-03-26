package chatserver;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
   
public interface IChatServer extends Remote {
    public boolean login(IChatClient client) throws RemoteException;
   
    public void publish(String s) throws RemoteException;
   
    public ArrayList<IChatClient> getConnected() throws RemoteException;
}