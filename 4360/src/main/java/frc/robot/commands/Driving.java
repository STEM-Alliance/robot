package frc.robot.commands;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Drivetrain; 

/**
 *
 */
public class Driving extends CommandBase {

    Drivetrain driveSubsystem;
    public Driving(Drivetrain drive) {
        addRequirements(drive);
        driveSubsystem = drive;
    }

    // Called just before this Command runs the first time
    @Override
    public void initialize() {

    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    public void execute() {
        driveSubsystem.driveeeee(); 
        SmartDashboard.putString("test", "it worked");
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    public boolean isFinished() {
        return false; 
    }

}
