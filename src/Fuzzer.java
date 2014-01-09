import java.awt.*;
import java.awt.event.*;
import java.rmi.*;
import java.io.*;
import java.net.*;
import java.util.*;
import java.lang.*;
import java.rmi.registry.*;
import java.util.UUID;


public class Fuzzer
{
	SharedObject sentence;

	public static void main(String argv[])
    {
		// initialize the system
		Client.init();

		// look up the IRC object in the name server
		// if not found, create it, and register it in the name server
		SharedObject s = Client.lookup("IRC");
		if (s == null)
        {
			s = Client.create(new Sentence());
			Client.register("IRC", s);
		}

		// create the graphical part
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
                if((s != null && ! s.equals(s2)) || s2 == null)
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
