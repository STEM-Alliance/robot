package org.wfrobotics.reuse.subsystems.motors;

import org.wfrobotics.reuse.subsystems.motors.PositionMotor.Builder;

public class PositionSubsystem
{
    private final PositionMotor motor;
    private final double[] positions;
    private final double tolerance;
    
//    public <T extends PositionMotor, B extends Builder<T, B>> PositionSubsystem(Builder<T, B> builder)
    public PositionSubsystem(Builder<?, ?> builder)
    {
        System.out.println("Builder passed to subsystem: " + builder);
        System.out.println("T motor: " + builder.motor);
        System.out.println("B thisBuilder: " + builder.thisBuilder.getClass());
        System.out.println("----------------------------------");
        
        motor = builder.build();
        positions = motor.positions;
        tolerance = motor.tolerance;
        
        System.out.println("Motor from builder: " + motor);
        System.out.println("positions[1]: " + motor.positions[1]);
        System.out.println("tolerance: " + motor.tolerance);
        System.out.println("----------------------------------");
    }

    protected void initDefaultCommand()
    {

    }

    public boolean atPosition(int index)
    {
        boolean result = false;
        
        if(index < positions.length)
        {
            result = Math.abs(positions[index] - motor.get()) < tolerance;
        }
        
        return result;
    }
}
