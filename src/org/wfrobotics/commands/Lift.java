package org.wfrobotics.commands;

import org.wfrobotics.hardware.led.LEDs;
import org.wfrobotics.hardware.led.LEDs.Effect;
import org.wfrobotics.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.GenericHID.RumbleType;
import edu.wpi.first.wpilibj.command.Command;

public class Lift extends Command 
{
    public final double TIMEOUT_NO_GEAR = 1;
    public final double TIMEOUT_RUMBLE = 1;
    public final double SAMPLES_UNTIL_LIFT = 5;
    public final boolean isManual;
    
    public double timeLastSensed;
    public double samplesWithGear;
    
    public Lift()
    {
        this(false);
    }
    public Lift(boolean manual)
    {        
        requires(Robot.lifterSubsystem);
        isManual = manual;
        samplesWithGear = 0;
    }
    
    @Override
    protected void initialize()
    {
        if (isManual)
        {
            Robot.leds.set(new Effect(EFFECT_TYPE.SOLID, LEDs.ORANGE, 1));
        }
        
        timeLastSensed = timeSinceInitialized() - TIMEOUT_NO_GEAR;  // Start in the down state
    }
    
    @Override
    protected void execute()
    {
        boolean direction;
        float rumble;
        double now = timeSinceInitialized();

        if (isManual)
        {
            direction = false;
            rumble = (Robot.lifterSubsystem.hasGear()) ? 1 : 0;
        }
        else
        {       
            if (Robot.lifterSubsystem.hasGear())
            {
                direction = (++samplesWithGear > SAMPLES_UNTIL_LIFT);  // How many cycles have we had a gear?
                rumble = (now - timeLastSensed > TIMEOUT_RUMBLE) ? 1 : 0;
                timeLastSensed = now;
            }
            else
            {
                direction = now - timeLastSensed < TIMEOUT_NO_GEAR;  // How long since we had a gear?
                rumble = (now - timeLastSensed < TIMEOUT_RUMBLE) ? 1 : 0;
            }
        }
        
        OI.xboxMan.setRumble(RumbleType.kLeftRumble, rumble);
        OI.xboxMan.setRumble(RumbleType.kRightRumble, rumble);
        OI.xboxDrive.setRumble(RumbleType.kLeftRumble, rumble);
        OI.xboxDrive.setRumble(RumbleType.kRightRumble, rumble);
        
        Robot.lifterSubsystem.set(direction);
    }

    @Override
    protected boolean isFinished()
    {
        return false;
    }
    
    protected void end()
    {
        if (isManual)
        {
            Robot.leds.set(new Effect(EFFECT_TYPE.SOLID, LEDs.GREEN, 1));
        }
    }
    
    protected void interrupted()
    {
        end();
    }
}
