package com.taurus.controller.macro;

import java.io.FileWriter;
import java.io.IOException;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Timer;

public class MacroRecord {
    
    private FileWriter m_writer;
    private double m_startTime;
    
    private int m_joystickCount;
    private int m_axisCountMax;
    private int m_povCountMax;
    
    private DriverStation m_ds;
    
    public MacroRecord(String filename,
                       int joystickCount,
                       int axisCountMax,
                       int povCountMax) throws IOException
    {
        m_writer = new FileWriter(filename);

        m_joystickCount = joystickCount;
        m_axisCountMax = axisCountMax;
        m_povCountMax = povCountMax;

        m_writer.append("" + m_joystickCount);
        m_writer.append("," + m_axisCountMax);
        m_writer.append("," + m_povCountMax);
        m_writer.append("\n");
        
        m_ds = DriverStation.getInstance();
        
        m_startTime = Timer.getFPGATimestamp();
    }

    public boolean Record()
    {
        if(m_writer != null)
        {
            try
            {
                // first write time change
                m_writer.append("" + (Timer.getFPGATimestamp() - m_startTime));

                for (int joystick = 0; joystick < m_joystickCount; joystick++)
                {
                    // write the joysticks
                    for (int axis = 0; axis < m_axisCountMax; axis++)
                    {
                        m_writer.append("," + m_ds.getStickAxis(joystick, axis));
                    }
                    
                    // write the pov
                    for (int pov = 0; pov < m_povCountMax; pov++)
                    {
                        m_writer.append("," + m_ds.getStickPOV(joystick, pov));
                    }
                    
                    // write the buttons
                    m_writer.append("," + m_ds.getStickButtons(joystick));
                }
                
                // wrote correctly
                m_writer.append("\n");
                return true;
            }
            catch(Exception e)
            {
            }
        }
        return false;
    }
    
    public void Stop()
    {
        
    }
}
