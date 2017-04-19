package org.wfrobotics.reuse.subsystems.motorcontrol;

import edu.wpi.first.wpilibj.command.Subsystem;

public class PositionSubsystem extends Subsystem
{
    private final Motor smartMotor;
    private final double[] positions;
    private final double tolerance;
    
    public PositionSubsystem(PositionConfig config)
    {
        smartMotor = config.makeMotor();
        positions = config.positions;
        tolerance = config.tolerance;
    }

    protected void initDefaultCommand()
    {

    }

    public boolean atPosition(int index)
    {
        boolean result = false;
        
        if(index < positions.length)
        {
            result = Math.abs(positions[index] - smartMotor.get()) < tolerance;
        }
        
        return result;
    }
}
