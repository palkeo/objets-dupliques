import java.lang.reflect.*;
import java.util.Arrays;

public class Generator
{
    public static String cleanAutoImport(String className)
    {
        if(className.startsWith("java.lang."))
            return className.substring(10);

        return className;
    }

    public static String generateItf(Class c)
    {
        String[] ignored_methods = new String[] {"wait", "equals", "toString", "hashCode", "getClass", "notify", "notifyAll"};
        String src = new String();

        src += String.format("public interface %s\n", c.getName());
        src += "{\n";

        for(Method met : c.getMethods())
        {
            if(Modifier.isPublic(met.getModifiers()) && !Arrays.asList(ignored_methods).contains(met.getName()))
            {
                int nb_params = 0;
                src += String.format("\tpublic %s %s(", cleanAutoImport(met.getReturnType().getName()), met.getName());

                for(Class param : met.getParameterTypes())
                {
                    if(nb_params++ > 0) src += ",";
                    src += cleanAutoImport(param.getName());
                }

                src += ");\n";
            }
        }

        src += "\n";
        src += "\t// SharedObject methods\n";
        src += "\tpublic void lock_read();\n";
        src += "\tpublic void lock_write();\n";
        src += "\tpublic void unlock();\n";
        src += "}";

        return src;
    }
}
