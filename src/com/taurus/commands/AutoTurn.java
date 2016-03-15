
package com.taurus.commands;

import edu.wpi.first.wpilibj.command.Command;

import com.taurus.robot.Robot;

public class AutoTurn extends Command 
{
    /**
     * Used to translate a state (such as starting position on the field) into descriptor of how to move
     */
    public enum STATE_TURN
    {
        // TODO - DRL can we run at speed magnitude of 1? If not, what is the cap? Let's use that?
        POSITION_ONE(70),       // TODO - DRL try (.66, -.75, .75) or faster
        POSITION_TWO(60),       // TODO - DRL Determine ideal values
        POSITION_THREE(19),     // TODO - DRL Determine ideal values
        POSITION_FOUR(-8),      // TODO - DRL Determine ideal values
        POSITION_FIVE(-32),;    // TODO - DRL Determine ideal values

        private final double angle;
        
        private STATE_TURN(double angle) 
        {
            this.angle = angle;
        }
    }
    
    final double angle;
    boolean done;

    /**
     * Turn
     */
    public AutoTurn(STATE_TURN position)
    {
        requires(Robot.rockerDriveSubsystem);
        this.angle = position.angle;
    }
    
    /**
     * turn to a specific angle
     */
    public AutoTurn(double angle)
    {
        requires(Robot.rockerDriveSubsystem);
        this.angle = angle;
    }
    

    protected void initialize()
    {
        done = false;
        // Create our heading straight in front of our current position
        Robot.rockerDriveSubsystem.enableGyro(true);
        //Robot.rockerDriveSubsystem.zeroGyro(0); //TODO BDP Needed to zero here?
    }

    protected void execute() 
    {
        done = Robot.rockerDriveSubsystem.turnToAngle(angle);
    }

    protected boolean isFinished()
    {
        return done;
    }

    protected void end() 
    {
        Robot.rockerDriveSubsystem.driveRaw(0, 0);
    }

    protected void interrupted()
    {
        
    }
}
