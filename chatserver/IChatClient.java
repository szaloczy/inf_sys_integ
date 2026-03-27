package chatserver;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public interface IChatClient extends Remote{
    public void tell(String name) throws RemoteException;
    public String getName() throws RemoteException;
    public void broadcastNewUsers(ArrayList<IChatClient> users) throws RemoteException;
}
