import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.rmi.registry.*;
import java.util.HashMap;
import java.net.*;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

public class Client extends UnicastRemoteObject implements Client_itf
{
    private static String RMI_PATH = "//localhost/SharedObjects";
    public static final Logger log = Logger.getLogger("client");

    private static Client client;
    private static Server_itf server;
    private static HashMap<Integer, SharedObject> objects;
    private static ReentrantLock mutex;

	public Client() throws RemoteException
    {
        mutex = new ReentrantLock();
        objects = new HashMap<Integer, SharedObject>();
	}

///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	// initialization of the client layer
	public static void init()
    {
        try
        {
            client = new Client();
            server = (Server_itf)Naming.lookup(RMI_PATH);
        }
        catch(Exception ex)
        {
            throw new RuntimeException("Unable to create client.");
        }
	}

	// lookup in the name server
	public static SharedObject lookup(String name)
    {
        log.info(String.format("lookup \"%s\"", name));

        try
        {
            int so_id = server.lookup(name);
            SharedObject so = null;

            if(so_id >= 0)
            {
                mutex.lock();

                if(objects.containsKey(so_id))
                    so = objects.get(so_id);
                else
                {
                    so = new SharedObject(so_id);
                    objects.put(so.getId(), so);
                }

                mutex.unlock();
            }

            return so;
        }
        catch(RemoteException e)
        {
            throw new RuntimeException(name + " cannot be found");
        }
	}

	// binding in the name server
	public static void register(String name, SharedObject so)
    {
        log.info(String.format("register object %d as \"%s\"", so.getId(), name));

        try
        {
            server.register(name, so.getId());
        }
        catch(RemoteException e)
        {
            throw new RuntimeException("Erreur server.register");
        }
	}

	// creation of a shared object
	public static SharedObject create(Object o)
    {
        log.info("create object");

        try
        {
            SharedObject so = new SharedObject(server.create(o));

            mutex.lock();
            objects.put(so.getId(), so);
            mutex.unlock();

            return so;
        }
        catch(RemoteException e)
        {
            throw new RuntimeException("Erreur server.create");
        }
	}

/////////////////////////////////////////////////////////////
//    Interface to be used by the consistency protocol
////////////////////////////////////////////////////////////

	// request a read lock from the server
	public static Object lock_read(int id)
    {
        log.info(String.format("lock_read object %d from the server", id));

        try
        {
            return server.lock_read(id, client);
        }
        catch(RemoteException e)
        {
            throw new RuntimeException(e.toString());
        }
	}

	// request a write lock from the server
	public static Object lock_write(int id)
    {
        log.info(String.format("lock_write object %d from the server", id));

        try
        {
            return server.lock_write(id, client);
        }
        catch(RemoteException e)
        {
            throw new RuntimeException(e.toString());
        }
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException
    {
        SharedObject so;

        mutex.lock();
        so = objects.get(id);
        mutex.unlock();

        return so.reduce_lock();
	}

	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException
    {
        SharedObject so;

        mutex.lock();
        so = objects.get(id);
        mutex.unlock();

        so.invalidate_reader();
	}

	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException
    {
        SharedObject so;

        mutex.lock();
        so = objects.get(id);
        mutex.unlock();

        return so.invalidate_writer();
	}
}
