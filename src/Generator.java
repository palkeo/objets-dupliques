import java.lang.reflect.*;
import java.util.Arrays;

public class Generator
{
    private final static String[] ignored_methods = new String[] {"wait", "equals", "toString", "hashCode", "getClass", "notify", "notifyAll"};

    public static String cleanAutoImport(String className)
    {
        if(className.startsWith("java.lang."))
            return className.substring(10);

        return className;
    }

    public static String generateItf(Class c)
    {
        String src = new String();

        src += String.format("public interface %s_itf extends SharedObject_itf\n", c.getName());
        src += "{\n";

        for(Method met : c.getMethods())
        {
            if(Modifier.isPublic(met.getModifiers()) && !Arrays.asList(ignored_methods).contains(met.getName()))
            {
                int nb_params = 0;
                src += String.format("\tpublic %s %s(", cleanAutoImport(met.getReturnType().getName()), met.getName());

                for(Class param : met.getParameterTypes())
                {
                    if(nb_params++ > 0) src += ", ";
                    src += String.format("%s a%d", cleanAutoImport(param.getName()), nb_params);
                }

                src += ");\n";
            }
        }

        src += "}";

        return src;
    }

    public static String generateStub(Class c)
    {
        String src = new String();

        src += String.format("public class %s_stub extends SharedObject", c.getName());
        src += String.format(" implements %s_itf, java.io.Serializable\n", c.getName());
        src += "{\n";
        src += String.format("\tpublic %s_stub(int id)\n", c.getName());
        src += "\t{\n";
        src += "\t\tsuper(id);\n";
        src += "\t}\n";

        for(Method met : c.getMethods())
        {
            if(Modifier.isPublic(met.getModifiers()) && !Arrays.asList(ignored_methods).contains(met.getName()))
            {
                int nb_params = 0;
                src += String.format("\tpublic %s %s(", cleanAutoImport(met.getReturnType().getName()), met.getName());

                for(Class param : met.getParameterTypes())
                {
                    if(nb_params++ > 0) src += ", ";
                    src += String.format("%s a%d", cleanAutoImport(param.getName()), nb_params);
                }

                src += ")\n";
                src += "\t{\n";
                src += String.format("\t\t%s o = (%s)obj;\n", c.getName(), c.getName());
                src += "\t\t";

                if(!met.getReturnType().equals(Void.TYPE))
                    src += "return ";

                src += String.format("o.%s(", met.getName());

                nb_params = 0;
                for(Class param : met.getParameterTypes())
                {
                    if(nb_params++ > 0) src += ", ";
                    src += String.format("a%d", nb_params);
                }

                src += ");\n";
                src += "\t}\n";
            }
        }

        src += "}";

        return src;
    }
}
