package com.taurus.subsystems;

import com.taurus.PIDController;
import com.taurus.Utilities;
import com.taurus.commands.AimerHold;
import com.taurus.commands.AimerLEDs;
import com.taurus.commands.AimerStop;
import com.taurus.hardware.MagnetoPotSRX;
import com.taurus.robot.RobotMap;
import com.taurus.vision.Target;
import com.taurus.vision.Vision;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.Relay.Direction;
import edu.wpi.first.wpilibj.Relay.Value;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class LEDsSubsystem extends Subsystem
{
   
    private Relay leds;

    public LEDsSubsystem()
    {
       
        
        leds = new Relay(RobotMap.PIN_RELAY_LEDS, Direction.kForward);
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new AimerLEDs(false));
    }
    
    
    public void enableLEDs(boolean enable)
    {
        if(enable)
        {
            leds.set(Value.kForward);
        }
        else
        {
            leds.set(Value.kOff);
        }
    }
}
