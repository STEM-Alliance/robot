package frc.robot.commands;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Config.Constants;
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
    private Relay cameraRelay; 
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
        final double p = prefs.getDouble("p", 0.2);
        final double i = prefs.getDouble("i", 0.0);
        final double d = prefs.getDouble("d", 0.0);

        pid = new PID(p, i, d, kVisionIZone, 0.02);
        
    }

    public void initialize()
    {
        drive.setBrake(false);
        drive.lightOn();
    }

    public void execute()
    {        vision.visionUpdate();

        final double turnCorrection = getVisionCorrection();
        final double turn = OI.herdJoystickRight.getX() + turnCorrection;
        
        drive.robotDrive.arcadeDrive(OI.herdJoystickRight.getX() * -1, -turn, true);
        

        SmartDashboard.putNumber("Error", turnCorrection);
    }

    public boolean isFinished()
    {
        return false;
    }

    private double getVisionCorrection()
    {
        double correction = 0.0;

        if (!visionAvailable())
        {
            pid.reset();
            return correction;
        }

        final double error = vision.getYawError();
        final double sign = Math.signum(error);
        correction = pid.update(sign);

        return correction;
    }

    private boolean driverPassive()
    {
        return Math.abs(OI.xbox.getRawAxis(1)) < 0.02 && Math.abs(OI.xbox.getRawAxis(4)) < 0.02;
    }

    private boolean 
    visionAvailable()
    {
        return vision.getModeTape() && vision.getInView();
    }
}
