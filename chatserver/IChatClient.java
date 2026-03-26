package chatserver;


import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

public interface IChatClient extends Remote{
    public void tell(String name) throws RemoteException;
    public String getName() throws RemoteException;
}
