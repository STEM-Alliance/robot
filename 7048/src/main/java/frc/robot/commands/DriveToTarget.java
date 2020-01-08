package frc.robot.commands;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Config.RobotContainer;
    import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Vision;

/** Turn until reaching the target, or get to the expected heading it should be at **/
public final class DriveToTarget extends CommandBase
{
    private static final double kVisionIZone = 25.0;  // Degrees Error

    private final Vision vision;
    private final Drivetrain drive;
    private final RobotContainer OI;
    private final PID pid;
    // private final SimplePID pid = new SimplePID(0.025, 0.004);  // Tuned pretty good for coast mode
    // private final SimplePID pid = new SimplePID(0.16, 0.00001);  // Tuned well for brake mode

    public DriveToTarget(Vision vision, Drivetrain drive, RobotContainer OI)
    {
        addRequirements(drive);
        addRequirements(vision);

        this.vision = vision;
        this.drive = drive;
        this.OI = OI;
        
        final Preferences prefs = Preferences.getInstance();
        final double p = prefs.getDouble("p", 0.055);
        final double i = prefs.getDouble("i", 0.0002);
        final double d = prefs.getDouble("d", 0.0);

        pid = new PID(p, i, d, kVisionIZone, 0.02);
    }

    public void initialize()
    {
        drive.setBrake(false);
    }

    public void execute()
    {
        final double turnCorrection = getVisionCorrection();
        final double turn = OI.xbox.getRawAxis(4) + turnCorrection;
        
        drive.robotDrive.arcadeDrive(OI.xbox.getRawAxis(1) * -1, turn, true);
    }

    public boolean isFinished()
    {
        return false;
    }

    private double getVisionCorrection()
    {
        double percentOutput = 0.0;

        if (!visionAvailable())
        {
            pid.reset();
            return percentOutput;
        }

        final double error = vision.getYawError();
        final double sign = Math.signum(error);
        final double correction = pid.update(sign);

        return percentOutput;
    }

    private boolean driverPassive()
    {
        return Math.abs(OI.xbox.getRawAxis(1)) < 0.02 && Math.abs(OI.xbox.getRawAxis(4)) < 0.02;
    }

    private boolean visionAvailable()
    {
        return vision.getModeTape() && vision.getInView();
    }
}
