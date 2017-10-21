package org.wfrobotics.robot.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.LED;
import org.wfrobotics.robot.subsystems.Lifter.POSITION;

import edu.wpi.first.wpilibj.command.Command;

public class Lift extends Command
{
    public enum MODE { AUTOMATIC, DOWN, UP };
    public final double TIMEOUT_NO_GEAR = 1;
    public final double TIMEOUT_RUMBLE = 1;
    public final double SAMPLES_UNTIL_LIFT = 5;

    public MODE mode = MODE.AUTOMATIC;

    public double timeLastSensed;
    public double samplesWithGear;

    public Lift()
    {
        this(MODE.AUTOMATIC);
    }

    public Lift(MODE mode)
    {
        requires(Robot.lifterSubsystem);
        this.mode = mode;
        samplesWithGear = 0;
    }

    protected void initialize()
    {
        timeLastSensed = timeSinceInitialized() - TIMEOUT_NO_GEAR;  // Start in the down state
    }

    protected void execute()
    {
        POSITION direction;
        boolean rumble;
        double now = timeSinceInitialized();

        if (mode != MODE.AUTOMATIC)
        {
            direction = (mode == MODE.UP) ? POSITION.TOP : POSITION.BOTTOM;
            rumble = Robot.lifterSubsystem.hasGear();
        }
        else
        {
            if (Robot.lifterSubsystem.hasGear())
            {
                direction = (++samplesWithGear > SAMPLES_UNTIL_LIFT) ? POSITION.TOP : POSITION.TRANSPORT;  // How many cycles have we had a gear?
                rumble = now - timeLastSensed > TIMEOUT_RUMBLE;
                timeLastSensed = now;
            }
            else
            {
                direction = (now - timeLastSensed < TIMEOUT_NO_GEAR) ? POSITION.TOP : POSITION.TRANSPORT;  // How long since we had a gear?
                rumble = now - timeLastSensed < TIMEOUT_RUMBLE;
            }
        }

        IO.setLiftRumble(rumble);
        Robot.lifterSubsystem.set(direction);
    }

    protected boolean isFinished()
    {
        switch(mode)
        {
            case AUTOMATIC:
                return false;
            case DOWN:
                return (mode == MODE.AUTOMATIC) ? Robot.lifterSubsystem.atTransport() : Robot.lifterSubsystem.atBottom();
            case UP:
                return Robot.lifterSubsystem.atTop();
            default:
                return false;
        }
    }

    protected void end()
    {
        if (mode != MODE.AUTOMATIC)
        {
            LED.getInstance().set(LED.defaultLEDEffect);
            IO.setLiftRumble(false);
        }
    }

    protected void interrupted()
    {
        end();
    }
}
