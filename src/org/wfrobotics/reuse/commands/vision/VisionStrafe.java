package org.wfrobotics.reuse.commands.vision;

import org.wfrobotics.Utilities;
import org.wfrobotics.reuse.commands.drive.swerve.AutoTurn;
import org.wfrobotics.reuse.hardware.led.LEDs;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect;
import org.wfrobotics.reuse.hardware.led.LEDs.Effect.EFFECT_TYPE;
import org.wfrobotics.reuse.subsystems.vision.NetworkTableCamera;
import org.wfrobotics.reuse.utilities.PIDController;
import org.wfrobotics.robot.subsystems.LED;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class VisionStrafe extends CommandGroup
{
    public static class Config
    {
        public double p;
        public double i;
        public double d;
        public double max;
        public double deadband;
        public double invertError;
        public boolean debug;

        public Config(double p, double i, double d,  double maxOut, double deadband, boolean invertError, boolean debug)
        {
            this.p = p;
            this.i = i;
            this.d = d;
            max = maxOut;
            this.deadband = deadband;
            this.invertError = (invertError) ? -1 : 1;
            this.debug = debug;
        }
    }

    private final VisionDetect camera;
    private final AutoTurn drive;
    private final Config config;

    private PIDController pid;

    public VisionStrafe(NetworkTableCamera camera, Config config)
    {
        this.camera = new VisionDetect(camera);
        drive = new AutoTurn(0);
        this.config = config;

        addSequential(new VisionEnable(camera));
        addParallel(this.camera);
        addSequential(drive);
        addSequential(new VisionDisable(camera));
    }

    protected void initialize()
    {
        config.p = Preferences.getInstance().getDouble(getName() + "P", config.p);
        config.i = Preferences.getInstance().getDouble(getName() + "I", config.i);
        config.d = Preferences.getInstance().getDouble(getName() + "D", config.d);
        config.max = Preferences.getInstance().getDouble(getName() + "Max", config.max);

        LED.getInstance().set(new Effect(EFFECT_TYPE.OFF, LEDs.BLACK, 1));
        pid = new PIDController(config.p, config.i, config.d, config.max);
    }

    protected void execute()
    {
        double error = camera.getDistanceFromCenter() * config.invertError;
        double pidOutput;

        if(!camera.getIsFound())
        {
            return;
        }

        pidOutput = pid.update(error);

        drive.set(0, pidOutput, 0, -1);

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
