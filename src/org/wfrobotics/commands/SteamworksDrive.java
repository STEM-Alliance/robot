package org.wfrobotics.commands;

import org.wfrobotics.Utilities;
import org.wfrobotics.commands.drive.DriveSwerveHalo;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Led;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SteamworksDrive extends CommandGroup 
{
    public int ANGLE_INTAKE_OFF = 25;
    
    private IntakeSetup intake;
    private LED leds;
    
    private boolean onLeft = false;
    private boolean onRight = false;
    private double startLeft;
    private double startRight;
    
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
        setIntakes();
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
    
    public void setIntakes()
    {
        double angleDifference = Robot.driveSubsystem.getLastVector().getAngle();
        double vectorMag = Robot.driveSubsystem.getLastVector().getMag();
        
        angleDifference = -(angleDifference - 90);
        angleDifference = Utilities.wrapToRange(angleDifference, -180, 180);
       
        if(Math.abs(vectorMag) > .1)
        {
            if(angleDifference  < -ANGLE_INTAKE_OFF &&
               angleDifference  > (-180 + ANGLE_INTAKE_OFF))
            {
                onLeft = true;
                startLeft = Timer.getFPGATimestamp();
            }
            if (angleDifference > ANGLE_INTAKE_OFF &&
                    angleDifference  < (180 - ANGLE_INTAKE_OFF))
            {
                onRight = true;
                startRight = Timer.getFPGATimestamp();
            }
            
        }
        else
        {
            if ((Timer.getFPGATimestamp() - startLeft) > 1)
            {
                onLeft = false;   
            }
            if ((Timer.getFPGATimestamp() - startRight) > 1)
            {
                onRight = false;   
            }
        }
        printDash(angleDifference, onRight, onLeft);
        
        intake.set(onLeft, onRight);
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
