public class Server extends UnicastRemoteObject implements Server_itf
{
    public Server() throws RemoteException
    {
        super(0);    // required to avoid the 'mic' step, see below
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

        RmiServer obj = new RmiServer();
        Naming.rebind("//localhost/SharedObjects", obj);
        System.out.println("SharedObjects bound in registry");
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
