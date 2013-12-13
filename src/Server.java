import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;

public class Server extends UnicastRemoteObject implements Server_itf
{
	private static final long serialVersionUID = -888183490414628873L;
	
	private static String RMI_PATH = "//localhost/SharedObjects";

	private ArrayList<ServerObject> objects;
	private HashMap<String, ServerObject> name_mapping;

	public Server() throws RemoteException
    {
        super(0);
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
        Naming.rebind(RMI_PATH, obj);
        System.out.println("SharedObjects bound in registry");
    }

    public int lookup(String name) throws java.rmi.RemoteException
    {
        return name_mapping.get(name).getId();
    }

    public void register(String name, int id) throws java.rmi.RemoteException
    {
        name_mapping.put(name, objects.get(id));
    }

    public int create(Object o) throws java.rmi.RemoteException
    {
        // créer un serverobject, l'ajouter dans objects, et retourner l'id du ServerObject créé
    }

    public Object lock_read(int id, Client_itf client) throws java.rmi.RemoteException
    {
        object = objects.get(id);
        return object.lock_read(client);
    }

    public Object lock_write(int id, Client_itf client) throws java.rmi.RemoteException
    {
        object = objects.get(id);
        return object.lock_write(client);
    }
}
