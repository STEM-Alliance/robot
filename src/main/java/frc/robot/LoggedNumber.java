package frc.robot;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import edu.wpi.first.wpilibj.DataLogManager;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.util.datalog.DataLog;
import edu.wpi.first.util.datalog.DoubleLogEntry;

public class LoggedNumber {

    HashMap<String, Double> m_itemNames = new HashMap<String, Double>();
    DataLog m_log = DataLogManager.getLog();
    private static LoggedNumber m_instance;

    private LoggedNumber()
    {
    }

    public static LoggedNumber getInstance() {
        if (m_instance == null) {
            m_instance = new LoggedNumber();
        }
        return m_instance;
    }

    public void logNumber(String name, double value)
    {
        // Compatible with the SmartDashboard method
        logNumber(value, name);
    }

    public void logNumber(String name, double value, boolean sendToTelemetry)
    {
        // Compatible with the SmartDashboard method
        logNumber(value, name);
        if (sendToTelemetry)
        {
            SmartDashboard.putNumber(name, value);
        }
    }

    public void logNumber(double value, String name)
    {
        m_itemNames.put(name, value);

        // found = false;
        // for (int i = 0; i < m_itemNames.size(); i++)
        // {
        //     if (m_itemNames.get(i) == name)
        //     {
        //         m_items.get(i).append(value);
        //         found = true;
        //     }
        // }

        // if (!found)
        // {
        //     m_itemNames.add(name);
        //     m_items.add(new DoubleLogEntry(m_log, "7048/" + name));
        // }
    }
}
