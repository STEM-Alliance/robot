package org.wfrobotics.robot.commands;

//import org.wfrobotics.prototype.Robot;
//import org.wfrobotics.prototype.subsystems.Tank;
//import org.wfrobotics.reuse.math.control.CheesyDriveHelper;
//import org.wfrobotics.reuse.math.control.CheesyDriveHelper.DriveSignal;
import org.wfrobotics.reuse.subsystems.drive.TankSubsystem;
//import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.config.IO;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/** Sets ExampleSubsystem to a safe state */
public class DriveTeleop extends Command
{
    protected final TankSubsystem drive = TankSubsystem.getInstance();
    protected final IO io = IO.getInstance();
//    protected static final CheesyDriveHelper helper = new CheesyDriveHelper();

    public DriveTeleop()
    {
        requires(drive);
    }

    public void init()
    {
        

        SmartDashboard.putString("Controller", "Xbox");
    }

    protected void execute()
    {
        /*final DriveSignal s = helper.cheesyDrive(io.getThrottle(), io.getTurn(), io.getDriveQuickTurn(), false);
        drive.driveOpenLoopLeft(s.getLeft());
        drive.driveOpenLoopRight(s.getRight());*/
    	drive.driveOpenLoop(io.getLeft(), io.getRight());
 //   	drive.driveOpenLoop(io.getRight());
//    	drive.driveOpenLoop(io.getLeft());
    	SmartDashboard.putNumber("Left", io.getLeft());
    	SmartDashboard.putNumber("Right", io.getRight());
    }

    protected void end()
    {
        
        SmartDashboard.putString("Controller", "Joystick");
    }

    protected boolean isFinished()
    {
        return false;
    }
}