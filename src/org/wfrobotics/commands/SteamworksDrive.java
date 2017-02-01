package org.wfrobotics.commands;

import org.wfrobotics.Utilities;
import org.wfrobotics.commands.drive.DriveSwerveHalo;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Led;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SteamworksDrive extends CommandGroup 
{    
    public int ANGLE_INTAKE_OFF = 25;
    
    private IntakeSetup intake;
    private LED leds;
    
    public SteamworksDrive()
    {   
        intake = new IntakeSetup(false, false);
        leds = new LED(Led.HARDWARE.TOP, LED.MODE.OFF);
        
        addParallel(intake);
        addParallel(leds);
        addSequential(new DriveSwerveHalo());
    }
    
    protected void execute()
    {
        double angleDifference = Robot.driveSubsystem.getLastVector().getAngle();
        double vectorMag = Robot.driveSubsystem.getLastVector().getMag();
        
        setIntakes(angleDifference, vectorMag);
        setLEDs();
    }
    
    protected void end() 
    {
        Robot.intakeSubsystem.setSpeed(0, 0);
        Robot.ledSubsystem.setOn(Led.HARDWARE.ALL, false);
    }
    
    protected void interrupted()
    {
        end();
    }
    
    public void setIntakes(double angleDifference, double vectorMag)
    {
        angleDifference = -(angleDifference - 90);
        angleDifference = Utilities.wrapToRange(angleDifference, -180, 180);
        boolean right = angleDifference > ANGLE_INTAKE_OFF &&
                       angleDifference  < (180 - ANGLE_INTAKE_OFF);
        boolean left = angleDifference  < -ANGLE_INTAKE_OFF &&
                angleDifference  > (-180 + ANGLE_INTAKE_OFF);
        
        if(Math.abs(vectorMag) < .1)
        {
            left = false;
            right = false;
        }
        printDash(angleDifference, right, left);
        
        intake.set(left, right);
    }
    
    public void setLEDs()
    {
        if (Robot.driveSubsystem.isGearStored())
        {
            leds.set(LED.MODE.BLINK);
        }        
        else if (Robot.driveSubsystem.isSpringInGear())
        {
            leds.set(LED.MODE.SOLID);
        }
        else
        {
            leds.set(LED.MODE.OFF);
        }
    }

    public void printDash(double angleDifference, boolean right, boolean left)
    {
        SmartDashboard.putNumber("angleDifference", angleDifference);
        SmartDashboard.putBoolean("intakeonright", right);
        SmartDashboard.putBoolean("intakeonleft", left);
    }
}
