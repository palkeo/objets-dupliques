import java.util.UUID;

public class TreeFuzzer
{
    SharedObject sentence;

    public static void main(String argv[])
    {
        Client.init();

        Tree_itf s = (Tree_itf)Client.lookup("Tree1");
        if (s == null)
        {
            Tree_itf s2 = (Tree_itf)Client.create(new Tree());
            s = (Tree_itf)Client.create(new Tree());
            s.lock_write();
            s.setLeft((SharedObject)s2);
            s.unlock();
            Client.register("Tree", s);
        }

        new TreeFuzzer(s);
    }

    public TreeFuzzer(Tree_itf so)
    {
        so.lock_read();
        while(true)
        {
            so.getLeft().lock_read();
            String s = null;
            for(int i = 0; i < 100; i++)
            {
                String s2 = ((Tree_itf) so.getLeft()).getLabel();
                if(s2 == null || (s != null && !s.equals(s2)))
                {
                    throw new RuntimeException("Inconsistency detected.");
                }
                s = s2;
            }
            so.getLeft().unlock();
            so.getLeft().lock_write();
            ((Tree_itf) so.getLeft()).setLabel(UUID.randomUUID().toString());
            so.getLeft().unlock();
        }

    }
}
