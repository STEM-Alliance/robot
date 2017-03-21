package org.wfrobotics.commands;

import org.wfrobotics.PIDController;
import org.wfrobotics.Utilities;
import org.wfrobotics.Vector;
import org.wfrobotics.commands.drive.AutoDrive;
import org.wfrobotics.hardware.led.LEDs;
import org.wfrobotics.hardware.led.LEDs.Effect;
import org.wfrobotics.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionGearPivot extends CommandGroup
{
    private DetectGear camera;
    private AutoDrive drive;
    private PIDController pid;

    private boolean done;
    
    public VisionGearPivot()
    {
        camera = new DetectGear(DetectGear.MODE.GETDATA);
        drive = new AutoDrive(0);

        addParallel(camera);
        addSequential(drive);
    }

    protected void initialize()
    {
        double p = Preferences.getInstance().getDouble("PivotP", 1.8);
        double i = Preferences.getInstance().getDouble("PivotI", 0);
        double d = Preferences.getInstance().getDouble("PivotD", 10);
        pid = new PIDController(p, i, d, .35);
        Robot.leds.set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
        done = false;
    }

    protected void execute()
    {
        double distanceFromCenter = camera.getDistanceFromCenter();
        double errorX;

        if(!camera.getIsFound())
        {
            done = true;
            return;
        }
        
        errorX = -pid.update(distanceFromCenter);
        
        drive.set(0, 0, errorX, -1);

        Utilities.PrintCommand("VisionGearDetect", this, camera.getIsFound() + "");
        SmartDashboard.putNumber("GearDistanceX", distanceFromCenter);
        SmartDashboard.putBoolean("GearFound", camera.getIsFound());
    }

    protected boolean isFinished() 
    {
        double distanceFromCenter = camera.getDistanceFromCenter();
        
        SmartDashboard.putNumber("VisionWidth", distanceFromCenter);
        return done || !camera.getIsFound() || Math.abs(distanceFromCenter) < .125;
    }

    protected void end()
    {
        drive.endEarly();
    }

    protected void interrupted()
    {
        end();
    }
}
