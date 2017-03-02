package org.wfrobotics.commands;

import org.wfrobotics.Utilities;
import org.wfrobotics.commands.drive.DriveSwerve;
import org.wfrobotics.robot.OI;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Led;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SteamworksDrive extends CommandGroup 
{  
    private IntakeSetup intake;

    private double intakeLastOn;

    public SteamworksDrive()
    {   
        intake = new IntakeSetup(false);

        addParallel(intake);
        addSequential(new DriveSwerve(DriveSwerve.MODE.FUSION));
    }   

    protected void execute()
    {
        double angleDifference = Robot.driveSubsystem.getLastVector().getAngle();
        double vectorMag = Robot.driveSubsystem.getLastVector().getMag();
        boolean intakeOn;

        angleDifference = -(angleDifference - 90);
        angleDifference = Utilities.wrapToRange(angleDifference, -180, 180);

        // Restart the intake timers whenever we move in that direction
        if(Math.abs(vectorMag) > .1 &&
                angleDifference > Constants.INTAKE_OFF_ANGLE &&
                angleDifference  < (180 - Constants.INTAKE_OFF_ANGLE))
        {
            intakeLastOn = Timer.getFPGATimestamp();
        }

        if(OI.buttonPanelBlackBottom.get() || OI.buttonManY.get())
        {
            intakeOn = true;
        }
        else
        {
            // Keep the intakes for a while after we stop moving in that direction
            intakeOn = (Timer.getFPGATimestamp() - intakeLastOn) < Constants.INTAKE_OFF_TIMEOUT;
        }
        
        SmartDashboard.putNumber("angleDifference", angleDifference);
        SmartDashboard.putBoolean("intakeOn", intakeOn);

        intake.set(intakeOn);
    }

    protected void end() 
    {
        Robot.intakeSubsystem.setSpeed(0);
        Robot.ledSubsystem.setOn(Led.HARDWARE.ALL, false);
    }

    protected void interrupted()
    {
        end();
    }
}
