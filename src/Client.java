import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.util.HashMap;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf
{
    private static String RMI_PATH = "//localhost/SharedObjects";
    
    private static Server_itf server;
    
    private HashMap<Integer, SharedObject> objects;

	public Client() throws RemoteException
    {
		super();
	}

///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	// initialization of the client layer
	public static void init()
    {
	    try
	    {
			server = (Server_itf)Naming.lookup(RMI_PATH);
		}
	    catch (Exception e)
	    {
			throw new RuntimeException();
		}
	}

	// lookup in the name server
	public static SharedObject lookup(String name)
    {
	    // id_objet = server.lookup(name)
	    // return new SharedObject…
	}

	// binding in the name server
	public static void register(String name, SharedObject_itf so)
    {
	}

	// creation of a shared object
	public static SharedObject create(Object o)
    {

	}

/////////////////////////////////////////////////////////////
//    Interface to be used by the consistency protocol
////////////////////////////////////////////////////////////

	// request a read lock from the server
	public static Object lock_read(int id)
    {
	}

	// request a write lock from the server
	public static Object lock_write (int id)
    {
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException
    {
	}

	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException
    {
	}

	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException
    {
	}
}
