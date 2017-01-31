package org.wfrobotics.commands;

import org.wfrobotics.commands.drive.DriveSwerveHalo;
import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Intake;
import org.wfrobotics.subsystems.Led;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class SteamworksDrive extends CommandGroup 
{    
    public int ANGLE_INTAKE_OFF = 25;
    
    private IntakeSetup intakeL;
    private IntakeSetup intakeR;
    private LED leds;
    
    public SteamworksDrive()
    {   
        intakeL = new IntakeSetup(false, Intake.MOTOR.LEFT);
        intakeR = new IntakeSetup(false, Intake.MOTOR.RIGHT);
        leds = new LED(Led.HARDWARE.TOP, LED.MODE.OFF);
        
        addParallel(intakeL);
        addParallel(intakeR);
        addParallel(leds);
        addSequential(new DriveSwerveHalo());
    }
    
    protected void execute()
    {
        double angleDifference = Robot.driveSubsystem.getLastVector().getAngle();
 
        setIntakes(angleDifference);
        setLEDs();
    }
    
    protected void end() 
    {
        Robot.intakeSubsystem.setSpeed(0, Intake.MOTOR.RIGHT);
        Robot.intakeSubsystem.setSpeed(0, Intake.MOTOR.LEFT);
        Robot.ledSubsystem.setOn(Led.HARDWARE.ALL, false);
    }
    
    protected void interrupted()
    {
        end();
    }
    
    public void setIntakes(double angleDifference)
    {
        boolean right = angleDifference > ANGLE_INTAKE_OFF &&
                       angleDifference  < 180 - ANGLE_INTAKE_OFF;
        boolean left = angleDifference  < -ANGLE_INTAKE_OFF &&
                angleDifference  > 180 + ANGLE_INTAKE_OFF;
        
        intakeR.set(right);
        intakeL.set(left);
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
}
