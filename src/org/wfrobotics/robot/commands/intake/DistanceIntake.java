package org.wfrobotics.robot.commands.intake;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DistanceIntake extends Command
{

    /*
     * when the block is further away from the sensor the motors are at speed x
     * when it comes closer to the sensor we want to ramp down the motors to speed 2/3x
     * when distances is equal to 0, the motor speed is set to 0
     *
     */
    public final double distanceToBlock = 20; // (in centmeters)
    /**
     * This command gets the distance from the subsystem and based on that distance
     * drive the motors at different speeds
     */
    public DistanceIntake()
    {
        requires(Robot.intakeSubsystem);
    }
    protected void execute()
    {
        SmartDashboard.putNumber("Distance:", Robot.intakeSubsystem.getDistance());
        Robot.intakeSubsystem.pushToRobotState();

        if (Robot.intakeSubsystem.getDistance() <= distanceToBlock)
        {
            Robot.intakeSubsystem.stopMotor();
            Robot.intakeSubsystem.pushHasCube(true);
        }
        else
        {
            Robot.intakeSubsystem.setMotor(0.1);
        }
    }
    protected boolean isFinished()
    {
        return false;
    }
}
