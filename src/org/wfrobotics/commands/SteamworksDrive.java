package org.wfrobotics.commands;

import org.wfrobotics.Utilities;
import org.wfrobotics.commands.drive.DriveSwerve;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Led;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SteamworksDrive extends CommandGroup 
{
    private final int INTAKE_OFF_ANGLE = 25;
    private final int INTAKE_OFF_TIMEOUT = 1;
    
    private IntakeSetup intake;
    private LED leds;
    
    private double intakeStartL;
    private double intakeStartR;
    
    public SteamworksDrive()
    {   
        intake = new IntakeSetup(false, false);
        leds = new LED(Led.HARDWARE.TOP, LED.MODE.OFF);
        
        addParallel(intake);
        addParallel(leds);
        addSequential(new DriveSwerve(DriveSwerve.MODE.HALO));
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
        boolean onRight;
        boolean onLeft;
        
        angleDifference = -(angleDifference - 90);
        angleDifference = Utilities.wrapToRange(angleDifference, -180, 180);
       
        // Restart the intake timers whenever we move in that direction
        if(Math.abs(vectorMag) > .1)
        {
            if(angleDifference  < -INTAKE_OFF_ANGLE &&
               angleDifference  > (-180 + INTAKE_OFF_ANGLE))
            {
                intakeStartL = Timer.getFPGATimestamp();
            }
            
            if (angleDifference > INTAKE_OFF_ANGLE &&
                angleDifference  < (180 - INTAKE_OFF_ANGLE))
            {
                intakeStartR = Timer.getFPGATimestamp();
            }            
        }
        
        // Keep the intakes for a while after we stop moving in that direction
        onLeft = (Timer.getFPGATimestamp() - intakeStartL) < INTAKE_OFF_TIMEOUT;
        onRight = (Timer.getFPGATimestamp() - intakeStartR) < INTAKE_OFF_TIMEOUT;
        
        printDash(angleDifference, onRight, onLeft);
        
        intake.set(onLeft, onRight);
    }
    
    public void setLEDs()
    {
        if (Robot.driveSubsystem.isSpringInGear())
        {
            leds.set(LED.MODE.SOLID);
        }
        else if (Robot.driveSubsystem.isGearStored())
        {
            leds.set(LED.MODE.BLINK);
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
