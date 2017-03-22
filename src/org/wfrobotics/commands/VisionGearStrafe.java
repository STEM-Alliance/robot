package org.wfrobotics.commands;

import org.wfrobotics.PIDController;
import org.wfrobotics.Utilities;
import org.wfrobotics.commands.drive.AutoDrive;
import org.wfrobotics.hardware.led.LEDs;
import org.wfrobotics.hardware.led.LEDs.Effect;
import org.wfrobotics.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionGearStrafe extends CommandGroup 
{
    private DetectGear camera;
    private AutoDrive drive;
    private PIDController pid;

    private boolean done;
    
    public VisionGearStrafe()
    {
        camera = new DetectGear(DetectGear.MODE.GETDATA);
        drive = new AutoDrive(0);

        addParallel(camera);
        addSequential(drive);
    }

    protected void initialize()
    {
        double p = Preferences.getInstance().getDouble("StrafeP", 1.85);
        double i = Preferences.getInstance().getDouble("StrafeI", .0001);
        double d = Preferences.getInstance().getDouble("StrafeD", 10);
        double max = Preferences.getInstance().getDouble("StrafeMax", .6);
        pid = new PIDController(p, i, d, max);
        Robot.leds.set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
        done = false;
    }

    protected void execute()
    {
        double distanceFromCenter = -camera.getDistanceFromCenter();
        double errorX;

        if(!camera.getIsFound())
        {
            done = true;
            return;
        }
        
        errorX = -pid.update(distanceFromCenter);
        
        drive.set(0, errorX, 0, -1);

        Utilities.PrintCommand("VisionGearDetect", this, camera.getIsFound() + "");
        SmartDashboard.putNumber("GearDistanceX", distanceFromCenter);
        SmartDashboard.putBoolean("GearFound", camera.getIsFound());
    }

    protected boolean isFinished() 
    {
        double distanceFromCenter = camera.getDistanceFromCenter();
        
        SmartDashboard.putNumber("VisionWidth", distanceFromCenter);
        return done || !camera.getIsFound() || Math.abs(distanceFromCenter) < .2;
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
