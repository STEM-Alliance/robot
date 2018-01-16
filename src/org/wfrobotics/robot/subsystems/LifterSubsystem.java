package org.wfrobotics.robot.subsystems;

import org.wfrobotics.robot.config.RobotMap;

import com.ctre.CANTalon;
import com.ctre.phoenix.motorcontrol.ControlMode;

import edu.wpi.first.wpilibj.command.Subsystem;

public class LifterSubsystem extends Subsystem {

    CANTalon liftMtr;

    public  LifterSubsystem()
    {
        liftMtr = new CANTalon(RobotMap.LIFTER_MOTOR);
    }
    @Override
    protected void initDefaultCommand()
    {
    }
    /**
     * Sets the speed of the motor
     * TODO: make it work
     * @param speed
     */
    public void setSpeed(double speed)
    {
        liftMtr.set(ControlMode.Velocity, speed);
    }
    public double getSpeed()
    {
        return liftMtr.getOutputCurrent();
    }
    /**
     * Returns true when it has decided the lifter is at the top
     * TODO: Decide on how it does that! (probibly with getPosition() or a simple limit switch)
     * @return true
     */
    public boolean isAtTop()
    {
        return false;
    }
    /**
     * Returns true when it has decided the lifter is at the bottem of the elevator
     * TODO: Decide on how it does that! (probibly with getPosition() or a simple limit switch)
     * @return true
     */
    public boolean isAtBottem()
    {
        return true;
    }
    /**
     * Returns the position the lifter is at on the elevator
     *      TODO: 
     *          -decide what unit it is going to output (inches or hogsheads)
     *          -What is going to get the position what sensor?
     * @return
     */
    public double getPosition()
    {
        return 0.1;
    }
    
}
