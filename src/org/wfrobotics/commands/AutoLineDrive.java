package org.wfrobotics.commands;

import org.wfrobotics.PIDController;
import org.wfrobotics.Vector;
import org.wfrobotics.commands.AutoDrive;

public class AutoLineDrive extends AutoDrive
{
    private final double signX;  // Save which way X commands are mirrored for this alliance
    private final PIDController linePID;  // TODO can we use a PID to track the line? Do we need two?
    
    public AutoLineDrive(double speedX, double speedY, double speedR, int heading, int timeout)
    {
        super(speedX, speedY, speedR, heading, timeout);
        
        signX = (speedX > 0) ? 1:-1;
        linePID = new PIDController(1, 0, 0, .7, .3);
    }

    @Override
    protected void execute()
    {
        applyLineSensors();  // Adjust to track the line
        
        super.execute();  // Normal autodrive
    }
    
    @Override
    protected boolean isFinished()
    {
        return isTimedOut();  // TODO Finish based on one of the distance sensors rather than a fixed time to the wall
    }
    
    private void applyLineSensors()
    {
        Vector newV = this.vector;
        double newR = this.rotate;
        
        // TODO do some math, use signX, change our drive parameters based on the line sensor(s)
        
        this.vector = newV;
        this.rotate = newR;
    }
}
