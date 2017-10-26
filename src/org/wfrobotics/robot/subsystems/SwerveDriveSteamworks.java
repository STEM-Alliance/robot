package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.swerve.SwerveSubsystem;
import org.wfrobotics.robot.commands.SteamworksDrive;
import org.wfrobotics.robot.config.RobotMap;

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
}
