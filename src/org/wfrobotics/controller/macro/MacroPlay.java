package org.wfrobotics.controller.macro;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import edu.wpi.first.wpilibj.Timer;

public class MacroPlay {
    
    private Scanner m_scanner;
    private double m_startTime;
    private boolean m_ready;
    private double m_nextSampleTime;

    private int m_joystickCount;
    private int m_axisCountMax;
    private int m_povCountMax;
    
    private MacroDriverStation m_mds;
    
    public MacroPlay(String filename) throws FileNotFoundException
    {
        m_scanner = new Scanner(new File(filename));
        
        // comma or new line delimiter
        m_scanner.useDelimiter(",|\\n");
        
        m_ready = true;

        MacroDriverStation.setUseMacros(true);
        m_mds = (MacroDriverStation) MacroDriverStation.getInstance();
        
        m_joystickCount = m_scanner.nextInt();
        m_axisCountMax = m_scanner.nextInt();
        m_povCountMax = m_scanner.nextInt();

        m_startTime = Timer.getFPGATimestamp(); 
    }
    
    public boolean Play()
    {
        // if csv file has a double to read next
        if (m_scanner != null)
        {
            if(m_scanner.hasNextDouble())
            {
                // if we need to grab the next sample time
                if(m_ready)
                {
                    //take next value
                    m_nextSampleTime = m_scanner.nextDouble();
                }
                
                m_ready = false;
                
                double timeDiff = m_nextSampleTime - (Timer.getFPGATimestamp() - m_startTime);
                
                // if we've reached the time,
                if (timeDiff <= 0)
                {
                    // set all the values
                    for (int joystick = 0; joystick < m_joystickCount; joystick++)
                    {
                        // write the joysticks
                        for (int axis = 0; axis < m_axisCountMax; axis++)
                        {
                            m_mds.setStickAxis(joystick, axis, m_scanner.nextDouble());
                        }
                        
                        // write the pov
                        for (int pov = 0; pov < m_povCountMax; pov++)
                        {
                            m_mds.setStickPOV(joystick, pov, m_scanner.nextInt());
                        }
                        
                        // write the buttons
                        m_mds.setStickButtons(joystick, m_scanner.nextInt());
                    }
                    
                    // ready for the next sample
                    m_ready = true;
                }
            }
            else
            {
                // no samples left
                m_scanner.close();
                m_scanner = null;
                return false;
            }
            
            // still more samples to read
            return true;
        }
        else
        {
            return false;
        }
    }
}
