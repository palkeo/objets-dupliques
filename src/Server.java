import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;

public class Server extends UnicastRemoteObject implements Server_itf
{
	private static final long serialVersionUID = -888183490414628873L;
	
	private ArrayList<ServerObject> objects;

	public Server() throws RemoteException
    {
        super(0);    // required to avoid the 'mic' step, see below
    }

    public static void main(String args[]) throws Exception
    {
        System.out.println("RMI server started");

        try
        { 
            //special exception handler for registry creation
            LocateRegistry.createRegistry(1337); 
            System.out.println("java RMI registry created.");
        }
        catch (RemoteException e)
        {
            //do nothing, error means registry already exists
            System.out.println("java RMI registry already exists.");
        }

        //Instantiate RmiServer
        Server obj = new Server();

        // Bind this object instance to the name "RmiServer"
        Naming.rebind("//localhost/SharedObjects", obj);
        System.out.println("PeerServer bound in registry");
    }


    public int lookup(String name) throws java.rmi.RemoteException
    {
    }

    public void register(String name, int id) throws java.rmi.RemoteException
    {
    }

    public int create(Object o) throws java.rmi.RemoteException
    {
    }

    public Object lock_read(int id, Client_itf client) throws java.rmi.RemoteException
    {
    }

    public Object lock_write(int id, Client_itf client) throws java.rmi.RemoteException
    {
    }
}
