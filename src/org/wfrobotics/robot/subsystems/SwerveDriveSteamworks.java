package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSubsystem;
import org.wfrobotics.robot.commands.SteamworksDrive;
import org.wfrobotics.robot.config.RobotMap;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Servo;

public class SwerveDriveSteamworks extends SwerveSubsystem 
{
    private final double ANGLE_GEAR_HOPPER_DEPLOYED = 180;
    private final double ANGLE_GEAR_HOPPER_STARTING = 0;
    
    private Servo servoGearIntake;
    
    public SwerveDriveSteamworks()
    {
        servoGearIntake = new Servo(RobotMap.PWM_SERVO_GEAR_INTAKE);
    }

    @Override
    public void initDefaultCommand() 
    {
        setDefaultCommand(new SteamworksDrive());
    }
    
    public void setGearHopper(boolean deploy)
    {
        double angle = (deploy) ? ANGLE_GEAR_HOPPER_DEPLOYED : ANGLE_GEAR_HOPPER_STARTING;
        
        servoGearIntake.setAngle(angle);
    }

    /**
     * Tell if the subsystem senses it possesses a stored gear
     * @return Do we have a stored gear?
     */
    public boolean isGearStored()
    {
        DriverStation.reportWarning("SteamworksDrive is gear stored not implemented yet", true);
        
        return false;  // TODO DRL return sensor feedback
    }
    
    /**
     * Tell if the subsystem senses the airship spring is piercing the stored gear
     * @return Is the spring piercing the stored gear?
     */
    public boolean isSpringInGear()
    {
        DriverStation.reportWarning("SteamworksDrive is spring in gear not implemented yet", true);
        
        return false;  // TODO DRL return sensor feedback
    }

    public void setLastHeading(double gyroOffset)
    {
        lastHeading = gyroOffset;
    }
}
