package org.wfrobotics.commands;

import org.wfrobotics.PIDController;
import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.commands.drive.AutoDrive;
import org.wfrobotics.hardware.led.LEDs;
import org.wfrobotics.hardware.led.LEDs.Effect;
import org.wfrobotics.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionGearPivot extends CommandGroup
{
    private DetectGear camera;
    private AutoDrive drive;
    private PIDController pid;
    private boolean fieldRelative = true;

    private boolean done;
    
    public VisionGearPivot()
        {
        pid = new PIDController(2.5, 0.125, 0, .35);
        camera = new DetectGear(DetectGear.MODE.GETDATA);
        drive = new AutoDrive(0, 0, 0, -1, 999);

        addParallel(camera);
        addSequential(drive);
    }

    protected void initialize()
    {
        Robot.leds.set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
        fieldRelative = Robot.driveSubsystem.getFieldRelative();
        Robot.driveSubsystem.setFieldRelative(false);
        done = false;
    }

    protected void execute()
    {
        double distanceFromCenter = camera.getDistanceFromCenter();
        double speedR;

        if(camera.getIsFound())
        {
            done = true;
            return;
        }

        if(Math.abs(distanceFromCenter) < 15)
        {
            pid.resetError();
        }
        
        speedR = pid.update(distanceFromCenter);
        
        drive.set(0, 0, speedR, -1);

        Utilities.PrintCommand("VisionGearDetect", this, camera.getIsFound() + "");
        SmartDashboard.putNumber("GearDistanceX", distanceFromCenter);
        SmartDashboard.putBoolean("GearFound", camera.getIsFound());
    }

    protected boolean isFinished() 
    {
        double width = camera.getFullWidth();
        
        SmartDashboard.putNumber("VisionWidth", width);
        return done || Math.abs(width) < 15;
    }

    protected void end()
    {
        Robot.driveSubsystem.setFieldRelative(fieldRelative);
    }

    protected void interrupted()
    {
        end();
    }
}
