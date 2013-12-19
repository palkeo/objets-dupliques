import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

public class Server extends UnicastRemoteObject implements Server_itf
{
	private static final long serialVersionUID = -888183490414628873L;
	private static final String RMI_PATH = "//localhost/SharedObjects";
    public static final Logger log = Logger.getLogger("server");

	private ArrayList<ServerObject> objects;
	private HashMap<String, ServerObject> name_mapping;

	public Server() throws RemoteException
    {
        super(0);
        name_mapping = new HashMap<String, ServerObject>();
        objects = new ArrayList<ServerObject>();
    }

    public static void main(String args[]) throws Exception
    {
        System.out.println("RMI server started");

        try
        { 
            LocateRegistry.createRegistry(1337); 
            System.out.println("java RMI registry created.");
        }
        catch (RemoteException e)
        {
            System.out.println("java RMI registry already exists.");
        }

        Server obj = new Server();
        Naming.rebind(RMI_PATH, obj);
        System.out.println("SharedObjects bound in registry");
    }

    public int lookup(String name) throws java.rmi.RemoteException
    {
        log.info(String.format("lookup \"%s\"", name));
        ServerObject so = name_mapping.get(name);
        return so == null ? -1 : so.getId();
    }

    public void register(String name, int id) throws java.rmi.RemoteException
    {
        log.info(String.format("register object %d as \"%s\"", id, name));
        name_mapping.put(name, objects.get(id));
    }

    public synchronized int create(Object o) throws java.rmi.RemoteException
    {
        int id = objects.size();
        log.info(String.format("create object %d", id));
        ServerObject so = new ServerObject(id, o);
        objects.add(so);
        return id;
    }

    public Object lock_read(int id, Client_itf client) throws java.rmi.RemoteException
    {
        ServerObject object = objects.get(id);
        object.lock_read(client);
        return object.getObj();
    }

    public Object lock_write(int id, Client_itf client) throws java.rmi.RemoteException
    {
        ServerObject object = objects.get(id);
        object.lock_write(client);
        return object.getObj();
    }
}
