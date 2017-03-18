package org.wfrobotics.hardware.cal;

import org.wfrobotics.Utilities;

import edu.wpi.first.wpilibj.command.Command;

/**
 * Command to calibrate a Talon SRX's PIDF for closed-loop control
 * Instead of creating a Talon SRX in your subsystem, create a SpeedPIDMotor and call set() on that instead.
 * Put the physical motor under full load (ex: place a drive motor on carpet).
 * Then kick off this command. The motor will be commanded, so make sure it's safe to do that (ex: the robot will drive).
 * @author drlindne
 */
public abstract class SpeedPIDFCommand extends Command
{
    private final double DURATION_WORST_F;
    private final double DURATION_CLOSED_LOOP_ERROR;
    
    private final double TIME_END_WORST_F;
    private final double TIME_END_CLOSED_LOOP_ERROR;
    
    private double startTime;
    
    /**
     * Constructor
     * @param timeTestFullSpeed Time (s) the motor takes to ramp to full speed in either direction and then stay at full for a bit (suggest .5s)
     * @param timeTestFullRange Time (s) the motor speed can be fully swept over the entire range of speeds in either one of the directions (suggest 4s)
     */
    public SpeedPIDFCommand(double timeTestFullSpeed, double timeTestFullRange)
    {
        DURATION_WORST_F = timeTestFullSpeed;
        DURATION_CLOSED_LOOP_ERROR = timeTestFullRange;
        
        TIME_END_WORST_F = DURATION_WORST_F;
        TIME_END_CLOSED_LOOP_ERROR = TIME_END_WORST_F + DURATION_CLOSED_LOOP_ERROR;
    }
    
    protected void initialize()
    {
        startTime = timeSinceInitialized();
        setMotor(SpeedPIDFTalon.COMMAND.RESET_ERROR.get(), 0);
    }
    
    protected void execute()
    {
        double timeElapsed = timeSinceInitialized() - startTime;
        
        if (timeElapsed < TIME_END_WORST_F)
        {
            setMotor(SpeedPIDFTalon.COMMAND.FIND_WORST_F.get(), 1);
        }
        else if (timeElapsed < TIME_END_CLOSED_LOOP_ERROR)
        {
            double command = Utilities.scaleToRange(timeElapsed - TIME_END_WORST_F, TIME_END_WORST_F, TIME_END_CLOSED_LOOP_ERROR, 0, 1);
                    
            setMotor(SpeedPIDFTalon.COMMAND.FIND_CLOSED_LOOP_ERROR.get(), command);
        }
    }
    
    protected boolean isFinished()
    {
        return false;
    }
    
    public abstract void setMotor(int mode, double raw);
}
