package org.wfrobotics.commands.vision;

import org.wfrobotics.PIDController;
import org.wfrobotics.Utilities;
import org.wfrobotics.commands.drive.AutoDrive;
import org.wfrobotics.hardware.led.LEDs;
import org.wfrobotics.hardware.led.LEDs.Effect;
import org.wfrobotics.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.hardware.led.LEDs.LEDController;
import org.wfrobotics.vision.NetworkTableCamera;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionPivot extends CommandGroup
{
    public static class Config
    {
        public double p;
        public double i;
        public double d;
        public double max;
        public double deadband;
        public double invert;
        public boolean debug;
        
        public Config(double p, double i, double d,  double maxOut, double deadband, boolean invertPIDOutput, boolean debug)
        {
            this.p = p;
            this.i = i;
            this.d = d;
            this.max = maxOut;
            this.deadband = deadband;
            this.invert = (invertPIDOutput) ? -1 : 1;
            this.debug = debug;
        }
    }
    
    private final VisionDetect camera;
    private final AutoDrive drive;
    private final LEDController leds;
    private final Config config;

    private PIDController pid;
    
    public VisionPivot(NetworkTableCamera cameraDriver, LEDController leds, Config config)
    {
        camera = new VisionDetect(cameraDriver, VisionDetect.MODE.GETDATA);
        drive = new AutoDrive(0);
        this.leds = leds;
        this.config = config;
        
        addParallel(camera);
        addSequential(drive);
    }

    protected void initialize()
    {
        config.p = Preferences.getInstance().getDouble(getName() + "P", config.p);
        config.i = Preferences.getInstance().getDouble(getName() + "I", config.i);
        config.d = Preferences.getInstance().getDouble(getName() + "D", config.d);
        config.max = Preferences.getInstance().getDouble(getName() + "Max", config.max);

        leds.set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
        pid = new PIDController(config.p, config.i, config.d, config.max);
    }

    protected void execute()
    {
        double error = camera.getDistanceFromCenter();
        double pidOutput;

        if(!camera.getIsFound())
        {
            return;
        }
        
        pidOutput = pid.update(error) * config.invert;
        drive.set(0, 0, pidOutput, -1);

        if (config.debug)
        {
            Utilities.PrintCommand(getName() + "Detect", this, camera.getIsFound() + "");
            SmartDashboard.putNumber(getName() + "Error", error);
            SmartDashboard.putNumber(getName() + "PIDOutput", pidOutput);
        }
    }

    protected boolean isFinished() 
    {
        double error = camera.getDistanceFromCenter();
        
        return !camera.getIsFound() || Math.abs(error) < config.deadband;
    }

    protected void end()
    {
        drive.endEarly();  // TODO Remove? Do we need to cancel the command group instead?
    }

    protected void interrupted()
    {
        end();
    }
}