import java.rmi.RemoteException;
import java.util.LinkedList;

public class ServerObject {

    private enum State {
        RLT,
        WLT,
        NL
    };
    
    private int id;
    private State state;
    private LinkedList<Client_itf> client_locks;
    private Object obj;
    
    public ServerObject(int id, Object obj)
    {
        this.state = State.NL;
        this.client_locks = new LinkedList<Client_itf>();
        this.obj = obj;
        this.id = id;
    }
    
    public int getId()
    {
        return id;
    }
    
    public Object getObj()
    {
        return this.obj;
    }
    
    public synchronized void lock_read(Client_itf client)
    {
        if(state == State.WLT)
        {
            assert(client_locks.size() == 1);
            try
            {
                this.obj = client_locks.getFirst().reduce_lock(id);
            }
            catch(RemoteException e)
            {
                throw new RuntimeException("Client error.");
            }
        }
        state = State.RLT;
        if(! client_locks.contains(client))
            client_locks.add(client);
    }
    
    public synchronized void lock_write(Client_itf client)
    {
        if(state == State.WLT)
        {
            assert(client_locks.size() == 1);
            try
            {
                this.obj = client_locks.getFirst().invalidate_writer(id);
            }
            catch(RemoteException e)
            {
                throw new RuntimeException("Client error.");
            }
        }
        else if(state == State.RLT)
        {
            for(Client_itf c: client_locks)
            {
                try
                {
                    c.invalidate_reader(id);
                }
                catch (RemoteException e)
                {
                    throw new RuntimeException("Client error.");
                }
            }
        }
        client_locks.clear();
        client_locks.add(client);
        state = State.WLT;
    }

}
