import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf
{
	private static final long serialVersionUID = 8271196138090195418L;
	
	private Client_itf client;
	private int id;
	
	SharedObject(Client_itf client, int id)
	{
		this.client = client;
		this.id = id;
	}
	
	// invoked by the user program on the client node
	public void lock_read()
    {
	}

	// invoked by the user program on the client node
	public void lock_write()
    {
	}

	// invoked by the user program on the client node
	public synchronized void unlock()
    {
	}

	// callback invoked remotely by the server
	public synchronized Object reduce_lock()
    {
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader()
    {
	}

	public synchronized Object invalidate_writer()
    {
	}
}
