import java.util.Random;
import java.util.UUID;

public class RandFuzzer
{
	SharedObject sentence;

	public static void main(String argv[])
    {
		Client.init();

		SharedObject s = Client.lookup("IRC");
		if (s == null)
        {
			s = Client.create(new Sentence());
			Client.register("IRC", s);
		}

		new RandFuzzer(s);
	}

	public RandFuzzer(SharedObject so)
    {
        Random rand = new Random();

        while(true)
        {
            if(rand.nextFloat() < 0.7) /* read */
            {
                so.lock_read();
                String s = null;
                for(int i = 0; i < 100; i++)
                {
                    String s2 = ((Sentence)(so.obj)).read();
                    if(s2 == null || (s != null && !s.equals(s2)))
                    {
                        throw new RuntimeException("Inconsistency detected.");
                    }
                    s = s2;
                }

                if(rand.nextFloat() < 0.1) /* write */
                {
                    so.lock_write();
                    ((Sentence)so.obj).write(UUID.randomUUID().toString());
                }
                so.unlock();
            }
            else /* write */
            {
                so.lock_write();
                ((Sentence)so.obj).write(UUID.randomUUID().toString());
                so.unlock();
            }
            Thread.yield();
        }
	}
}
