package org.wfrobotics.reuse.controller.macro;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.hal.HAL;

/*
public class MacroDriverStation extends DriverStation {
    
    
    private static MacroDriverStation instance = new MacroDriverStation();
    
    private static boolean m_useMacros = false;
    
    private double[][] m_joystickAxes = new double[kJoystickPorts][HAL.kMaxJoystickAxes];
    private int[][] m_joystickPOVs = new int[kJoystickPorts][HAL.kMaxJoystickPOVs];
    private int[] m_joystickButtons = new int[kJoystickPorts];

    public static DriverStation getInstance() {
        if(m_useMacros)
        {
            return instance;
        }
        else
        {
            return DriverStation.getInstance();
        }
    }

    public static void setUseMacros(boolean useMacros)
    {
        m_useMacros = useMacros;
    }
    
    public static boolean getUseMacros()
    {
        return m_useMacros;
    }

    
    public synchronized double getStickAxis(int stick, int axis) {
        if(stick < 0 || stick >= kJoystickPorts) {
            throw new RuntimeException("Joystick index is out of range, should be 0-5");
        }

        if (axis < 0 || axis >= HAL.kMaxJoystickAxes) {
            throw new RuntimeException("Joystick axis is out of range");
        }

        if (axis >= m_joystickAxes[stick].length) {
            //reportJoystickUnpluggedError("WARNING: Joystick axis " + axis + " on port " + stick + " not available, check if controller is plugged in\n");
            return 0.0;
        }

        return m_joystickAxes[stick][axis];
    }

    public synchronized int getStickAxisCount(int stick){

        if(stick < 0 || stick >= kJoystickPorts) {
            throw new RuntimeException("Joystick index is out of range, should be 0-5");
        }
        
        return m_joystickAxes[stick].length;
    }

    public synchronized int getStickPOV(int stick, int pov) {
        if(stick < 0 || stick >= kJoystickPorts) {
            throw new RuntimeException("Joystick index is out of range, should be 0-5");
        }

        if (pov < 0 || pov >= HAL.kMaxJoystickPOVs) {
            throw new RuntimeException("Joystick POV is out of range");
        }

        if (pov >= m_joystickPOVs[stick].length) {
            //reportJoystickUnpluggedError("WARNING: Joystick POV " + pov + " on port " + stick + " not available, check if controller is plugged in\n");
            return -1;
        }

        return m_joystickPOVs[stick][pov];
    }

    public synchronized int getStickPOVCount(int stick){

        if(stick < 0 || stick >= kJoystickPorts) {
            throw new RuntimeException("Joystick index is out of range, should be 0-5");
        }

        return m_joystickPOVs[stick].length;
    }
    
    public synchronized int getStickButtons(final int stick) {
        if(stick < 0 || stick >= kJoystickPorts) {
            throw new RuntimeException("Joystick index is out of range, should be 0-3");
        }

        return m_joystickButtons[stick];
    }

    public synchronized boolean getStickButton(final int stick, byte button) {
        if(stick < 0 || stick >= kJoystickPorts) {
            throw new RuntimeException("Joystick index is out of range, should be 0-3");
        }
        
        if(button <= 0)
        {
            //reportJoystickUnpluggedError("ERROR: Button indexes begin at 1 in WPILib for C++ and Java\n");
            return false;
        }
        return ((0x1 << (button - 1)) & m_joystickButtons[stick]) != 0;
    }

    public synchronized int getStickButtonCount(int stick){

        if(stick < 0 || stick >= kJoystickPorts) {
            throw new RuntimeException("Joystick index is out of range, should be 0-5");
        }
        
        return 4 * 8;
    }
    
    public synchronized boolean isNewControlData() {
        return true;
    }
    
    
    public synchronized void setStickAxis(int stick, int axis, double val) {
        if(stick < 0 || stick >= kJoystickPorts) {
            throw new RuntimeException("Joystick index is out of range, should be 0-5");
        }

        if (axis < 0 || axis >= HAL.kMaxJoystickAxes) {
            throw new RuntimeException("Joystick axis is out of range");
        }

        if (axis >= m_joystickAxes[stick].length) {
            //reportJoystickUnpluggedError("WARNING: Joystick axis " + axis + " on port " + stick + " not available, check if controller is plugged in\n");
            return;
        }

        m_joystickAxes[stick][axis] = val;
    }

    public synchronized void setStickPOV(int stick, int pov, int val) {
        if(stick < 0 || stick >= kJoystickPorts) {
            throw new RuntimeException("Joystick index is out of range, should be 0-5");
        }

        if (pov < 0 || pov >= HAL.kMaxJoystickPOVs) {
            throw new RuntimeException("Joystick POV is out of range");
        }

        if (pov >= m_joystickPOVs[stick].length) {
            //reportJoystickUnpluggedError("WARNING: Joystick POV " + pov + " on port " + stick + " not available, check if controller is plugged in\n");
            return;
        }

        m_joystickPOVs[stick][pov] = val;
    }

    public synchronized void setStickButtons(final int stick, int val) {
        if(stick < 0 || stick >= kJoystickPorts) {
            throw new RuntimeException("Joystick index is out of range, should be 0-3");
        }

        m_joystickButtons[stick] = val;
    }

    public synchronized void setStickButton(final int stick, byte button, boolean val) {
        if(stick < 0 || stick >= kJoystickPorts) {
            throw new RuntimeException("Joystick index is out of range, should be 0-3");
        }
        
        if(button <= 0)
        {
            //reportJoystickUnpluggedError("ERROR: Button indexes begin at 1 in WPILib for C++ and Java\n");
            return;
        }
        
        if(val)
        {
            // set the bit
            m_joystickButtons[stick] |= 0x1 << (button - 1);
        }
        else
        {
            // clear the bit
            m_joystickButtons[stick] &= ~(0x1 << (button - 1));
        }
    }
}
*/
