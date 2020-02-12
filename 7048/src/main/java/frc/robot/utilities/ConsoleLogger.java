package frc.robot.utilities;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

public class ConsoleLogger implements Reportable
{
    private static final ConsoleLogger instance = new ConsoleLogger();
    private static final Logger delegateLogger = Logger.getLogger("SAFM");
    private static StringBuilder sb = new StringBuilder(100);

    static
    {
        delegateLogger.setUseParentHandlers(false);
        for (Handler hand : delegateLogger.getHandlers())
        {
            delegateLogger.removeHandler(hand);
        }
        delegateLogger.addHandler(new BufferingHandler());
        setLevel(Level.INFO);
    }

    public static ConsoleLogger getInstance()
    {
        return instance;
    }

    /** Change what gets printed for this logger **/
    public static synchronized void setLevel(Level l)
    {
        delegateLogger.setLevel(l);
    }

    public static synchronized void info(Object value)
    {
        delegateLogger.log(Level.INFO, "INFO: ", value);
    }

    public static synchronized void warning(Object value)
    {
        delegateLogger.log(Level.WARNING, "WARNING: ", value);
    }

    public static synchronized void error(Object value)
    {
        delegateLogger.log(Level.SEVERE, "ERROR: ", value);
    }

    /** Like error() but immediately reported rather than buffered */
    public static synchronized void defcon1(Object value)
    {
        delegateLogger.log(SAFMLevel.DEFCON1, "DEFCON1: ", value);
        instance.reportState();
    }

    /** Print out built up messages that passed the logger's filter since previous call */
    public synchronized void reportState()
    {
        if (sb.length() > 0)
        {
            System.out.println(sb.toString());
            sb = new StringBuilder(100);
        }
    }

    private static synchronized void push(String message)
    {
        sb.append(message);
    }

    private static class BufferingHandler extends Handler
    {
        @Override
        public void publish(LogRecord record)
        {
            Object[] value = record.getParameters();

            if(value == null || value.length == 0)
            {
                return;
            }
            push(record.getMessage() + "" + value[0] + "\n");
        }

        @Override
        public void flush() {}

        @Override
        public void close() throws SecurityException {}
    }

    private static class SAFMLevel extends Level
    {
        private static final long serialVersionUID = 1L;

        protected SAFMLevel(String name, int value, String resourceBundleName)
        {
            super(name, value, resourceBundleName);
        }

        public static final SAFMLevel DEFCON1 = new SAFMLevel("DEFCON1" , 1100, "SAFM");
    }
}
