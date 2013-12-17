import java.io.*;
import java.util.concurrent.locks.ReentrantLock;

public class SharedObject implements Serializable, SharedObject_itf
{
	private static final long serialVersionUID = 8271196138090195418L;

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
        if(this.state == State.NL)
        {
            this.obj = Client.lock_read(this.id);
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
	}

	// invoked by the user program on the client node
	public void lock_write()
    {
        if(this.state == State.NL || this.state == State.RLC || this.state == State.RLT)
        {
            this.obj = Client.lock_write(this.id);
        }

        this.state = State.WLT;
	}

	// invoked by the user program on the client node
	public void unlock()
    {
        if(this.state == State.RLT)
        {
            this.state = State.RLC;
        }
        else if(this.state == State.WLT || this.state == State.RLT_WLC)
        {
            this.state = State.WLC;
        }
	}

	// callback invoked remotely by the server
	public synchronized Object reduce_lock()
    {
        while(this.state == State.WLT)
        {
            // FIXME
            Thread.yield();
        }

        if(this.state == State.WLC)
        {
            this.state = State.RLC;
        }
        else if(this.state == State.RLT_WLC)
        {
            this.state = State.RLT;
        }

        return this.obj;
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader()
    {
        while(this.state == State.RLT || this.state == State.RLT_WLC)
        {
            // FIXME
            Thread.yield();
        }

        this.state = State.NL;
        this.obj = null; // to help debugging
	}

	// callback invoked remotely by the server
	public synchronized Object invalidate_writer()
    {
        while(this.state == State.WLT || this.state == State.RLT || this.state == State.RLT_WLC)
        {
            // FIXME
            Thread.yield();
        }

        this.state = State.NL;


        return this.obj;
	}
}
