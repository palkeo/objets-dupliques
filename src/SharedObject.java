import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

public class SharedObject implements Serializable, SharedObject_itf
{
	private static final long serialVersionUID = 8271196138090195418L;

    private ReentrantLock mutex = new ReentrantLock();

    private enum State {
        NL,
        RLC,
        WLC,
        RLT,
        WLT,
        RLT_WLC,
    };

	private int id;

    // public as it is directly acessed in the « irc » example.
    // don't ask me why...
	public Object obj;

	private State state;

	SharedObject(int id)
	{
	    this.state = State.NL;
		this.id = id;
		this.obj = null;
	}
	
    public int getId()
    {
        return this.id;
    }

	public Object getObj()
	{
	    return this.obj;
	}

	// invoked by the user program on the client node
	public void lock_read()
    {
        Client.log.info(String.format("lock_read object %d", this.id));

        mutex.lock();
        if(this.state == State.NL)
        {
            mutex.unlock();
            this.obj = Client.lock_read(this.id);
            mutex.lock();
            assert(this.state == State.NL);
            this.state = State.RLT;
        }
        else if(this.state == State.RLC)
        {
            this.state = State.RLT;
        }
        else if(this.state == State.WLC)
        {
            this.state = State.RLT_WLC;
        }
        mutex.unlock();
	}

	// invoked by the user program on the client node
	public void lock_write()
    {
        Client.log.info(String.format("lock_write object %d", this.id));

        mutex.lock();
        if(this.state == State.NL || this.state == State.RLC || this.state == State.RLT)
        {
            mutex.unlock();
            this.obj = Client.lock_write(this.id);
            mutex.lock();
        }

        this.state = State.WLT;
        mutex.unlock();
	}

	// invoked by the user program on the client node
	public void unlock()
    {
        Client.log.info(String.format("unlock object %d", this.id));

        mutex.lock();
        if(this.state == State.RLT)
        {
            this.state = State.RLC;
        }
        else if(this.state == State.WLT || this.state == State.RLT_WLC)
        {
            this.state = State.WLC;
        }
        mutex.unlock();
	}

	// callback invoked remotely by the server
	public Object reduce_lock()
    {
        Client.log.info(String.format("reduce_lock object %d", this.id));

        mutex.lock();
        while(this.state == State.WLT)
        {
            // FIXME
            mutex.unlock();
            Thread.yield();
            mutex.lock();
        }

        if(this.state == State.WLC)
            this.state = State.RLC;
        else if(this.state == State.RLT_WLC)
            this.state = State.RLT;

        mutex.unlock();
        return this.obj;
	}

	// callback invoked remotely by the server
	public void invalidate_reader()
    {
        Client.log.info(String.format("invalidate_reader object %d", this.id));

        mutex.lock();
        while(this.state == State.RLT || this.state == State.RLT_WLC || this.state == State.WLT)
        {
            // FIXME
            mutex.unlock();
            Thread.yield();
            mutex.lock();
        }

        this.state = State.NL;
        this.obj = null; // to help debugging
        mutex.unlock();
	}

	// callback invoked remotely by the server
	public Object invalidate_writer()
    {
        Client.log.info(String.format("invalidate_writer object %d", this.id));

        mutex.lock();
        while(this.state == State.WLT)
        {
            // FIXME
            mutex.unlock();
            Thread.yield();
            mutex.lock();
        }

        if(this.state == State.WLT)
            this.state = State.NL;
        else if(this.state == State.RLT_WLC)
            this.state = State.RLT;

        mutex.unlock();
        return this.obj;
	}
}
