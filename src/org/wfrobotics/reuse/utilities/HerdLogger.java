package org.wfrobotics.reuse.utilities;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class HerdLogger
{
    private static final HerdLogger temporaryDebuggingLogger = new HerdLogger("Temp");

    private final Logger delegateLogger;

    public HerdLogger(Class<?> c)
    {
        this(c.getName());
    }

    public HerdLogger(String sClass)
    {
        delegateLogger = Logger.getLogger(sClass);
        delegateLogger.setUseParentHandlers(false);
        for (Handler hand : delegateLogger.getHandlers())
        {
            delegateLogger.removeHandler(hand);
        }
        delegateLogger.addHandler(new SmartDashProducer());
    }

    /** For last minute debugging. Please delete or adjust into another log level if committing. **/
    public static void temp(String key, Object value)
    {
        temporaryDebuggingLogger.delegateLogger.log(Level.SEVERE, key, value);
    }

    /** For last minute debugging. Please delete or adjust log statement levels instead of committing **/
    public void setLevelTempDebug()
    {
        delegateLogger.setLevel(Level.FINE);
    }

    /** Change what gets printed for this logger **/
    public void setLevel(Level l)
    {
        delegateLogger.setLevel(l);
    }

    public void debug(String key, Object value)
    {
        delegateLogger.log(Level.FINE, key, value);
    }

    public void info(String key, Object value)
    {
        delegateLogger.log(Level.INFO, key, value);
    }

    public void warning(String key, Object value)
    {
        delegateLogger.log(Level.WARNING, key, value);
    }

    public void error(String key, Object value)
    {
        delegateLogger.log(Level.SEVERE, key, value);
    }

    static class SmartDashProducer extends Handler
    {
        @Override
        public void publish(LogRecord record)
        {
            Object[] smartDashFormat = record.getParameters();

            if(smartDashFormat == null || smartDashFormat.length == 0)
            {
                return;
            }

            //System.out.println(record.getMessage() + "" + smartDashFormat[0]);
            SmartDashboard.putString(record.getMessage(), smartDashFormat[0].toString());
        }

        @Override
        public void flush() {}

        @Override
        public void close() throws SecurityException {}
    }
}
