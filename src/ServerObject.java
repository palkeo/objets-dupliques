import java.util.LinkedList;

public class ServerObject {

    private enum State {
        RLT,
        WLT,
        NL
    };
    
    private int id;
    private State state;
    private LinkedList<Client> client_locks;
    private Object obj;
    
    public ServerObject(int id, Object obj)
    {
        this.state = State.NL;
        this.client_locks = new LinkedList<Client>();
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
    
    public synchronized void lock_read(Client client)
    {
    }
    
    public synchronized void lock_write(Client client)
    {
    }

}
