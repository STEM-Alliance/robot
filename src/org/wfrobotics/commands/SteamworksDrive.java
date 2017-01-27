package org.wfrobotics.commands;

import org.wfrobotics.commands.drive.DriveSwerveHalo;
import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Intake;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class SteamworksDrive extends CommandGroup 
{    
    public int ANGLE_INTAKE_OFF = 25;
    
    private IntakeSetup intakeL;
    private IntakeSetup intakeR;
    
    public SteamworksDrive() 
    {   
        intakeL = new IntakeSetup(false, Intake.MOTOR.LEFT);
        intakeR = new IntakeSetup(false, Intake.MOTOR.RIGHT);
        
        addParallel(intakeL);
        addParallel(intakeR); 
        addSequential(new DriveSwerveHalo());
    }
    
    protected void execute()
    {
        double lastHeading;
        double joystickAngle;
        double angleDifference;
        
        joystickAngle = -OI.DriveSwerveOI.getHaloDrive_Rotation();
        lastHeading = Robot.driveSubsystem.getLastHeading(); // TODO What are the units? We assume -180 to 180
        angleDifference = lastHeading - joystickAngle;
 
        setIntakeRight(angleDifference);
        setIntakeLeft(angleDifference);
        // TODO LEDs? Make them also depend on our pathing?
    }
    
    protected void end() 
    {
        Robot.intakeSubsystem.setSpeed(0, Intake.MOTOR.RIGHT);
        Robot.intakeSubsystem.setSpeed(0, Intake.MOTOR.LEFT);
    }
    
    protected void interrupted()
    {
        end();
    }
    
    public void setIntakeRight(double angleDifference)
    {
        boolean isOn = angleDifference > ANGLE_INTAKE_OFF &&
                       angleDifference  < 180 - ANGLE_INTAKE_OFF;
        
        intakeR.setOn(isOn);
    }
    
    public void setIntakeLeft(double angleDifference)
    {
        boolean isOn = angleDifference  < -ANGLE_INTAKE_OFF &&
                       angleDifference  > 180 + ANGLE_INTAKE_OFF;
        
        intakeL.setOn(isOn);
    }
}
