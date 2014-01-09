import java.util.UUID;


public class Fuzzer
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

		new Fuzzer(s);
	}

	public Fuzzer(SharedObject so)
    {
        while(true)
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
            so.unlock();
            //Thread.yield();
            /*
            try
            {
                Thread.sleep(1);
            }
            catch(Exception ex)
            {
            }
            */
            /*
            for(int i = 0; i < 100000; i++)
            {
                s = "trololo";
            }
            */
            so.lock_write();
            ((Sentence)so.obj).write(UUID.randomUUID().toString());
            so.unlock();
        }

	}
}
