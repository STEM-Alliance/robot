
package org.wfrobotics.commands.drive;

import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.drive.swerve.SwerveDriveSubsystem;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class DriveSwerveSingleWheelTest extends Command 
{
    private SendableChooser<Integer> testWheelChooser;
    
    public DriveSwerveSingleWheelTest() 
    {
        requires(Robot.driveSubsystem);
        
    }

    protected void initialize() 
    {
        testWheelChooser = new SendableChooser<Integer>();
        testWheelChooser.addDefault("Front Left", Integer.valueOf(0));
        testWheelChooser.addObject("Front Right", Integer.valueOf(1));
        testWheelChooser.addObject("Back Right", Integer.valueOf(2));
        testWheelChooser.addObject("Back Left", Integer.valueOf(3));
        SmartDashboard.putData("Test Wheel", testWheelChooser);
    }

    protected void execute() 
    {
        Utilities.PrintCommand("Drive", this);

        int i = ((Integer) testWheelChooser.getSelected()).intValue();

        //SmartDashboard.putNumber("Test Wheel", i);
        SmartDashboard.putNumber("Vel Ang", OI.DriveSwerveOI.getHaloDrive_Velocity().getAngle());
        SmartDashboard.putNumber("Vel Mag", OI.DriveSwerveOI.getHaloDrive_Velocity().getMag());
        
        Vector WheelActual = ((SwerveDriveSubsystem)Robot.driveSubsystem).getWheel(i).setDesired(
                OI.DriveSwerveOI.getHaloDrive_Velocity(),
                OI.DriveSwerveOI.getHighGearEnable(),
                false);
            
        // display in SmartDashboard
        SmartDashboard.putNumber("Test Wheel Mag Actual", WheelActual.getMag());
        SmartDashboard.putNumber("Test Wheel Angle Actual", WheelActual.getAngle());
    }

    protected boolean isFinished() 
    {
        return false;
    }

    protected void end() 
    {
    }

    protected void interrupted() 
    {
    }
}
