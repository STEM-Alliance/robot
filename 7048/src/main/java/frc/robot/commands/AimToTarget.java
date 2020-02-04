package frc.robot.commands;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Config.RobotContainer;
import frc.robot.subsystems.Aiming;
import frc.robot.subsystems.Vision;

/** Turn until reaching the target, or get to the expected heading it should be at **/
public final class AimToTarget extends CommandBase
{
    private static final double kVisionIZone = 25.0;  // Degrees Error

    private final Vision vision;
    private final Aiming aiming;
    private final RobotContainer OI;
    private final PID rotationPID;
    private final PID elevationPID;

    // private final SimplePID pid = new SimplePID(0.025, 0.004);  // Tuned pretty good for coast mode
    // private final SimplePID pid = new SimplePID(0.16, 0.00001);  // Tuned well for brake mode


    public AimToTarget(Vision vision, Aiming aiming, RobotContainer OI)
    {
        addRequirements(aiming);
        addRequirements(vision);

        this.vision = vision;
        this.aiming = aiming;
        this.OI = OI;
        
        final Preferences prefs = Preferences.getInstance();
        final double p = prefs.getDouble("p", 0.58);
        final double i = prefs.getDouble("i", 0.0);
        final double d = prefs.getDouble("d", 0.001);

        rotationPID = new PID(p, i, d, kVisionIZone, 0.02);
        elevationPID = new PID(p, i, d, kVisionIZone, 0.02);
    }

    public void initialize()
    {
    }

    public void execute()
    {
        // Cache new vision values
        vision.visionUpdate();

        // Get error correction for rotation and elevation
            // Get rotation correction
            final double rotationCorrection = calculateRotationCorrection();
            // Get elevation correction
            // Apply rotation and elevation correction
        
        // Move towards target

        aiming.moveRotation(rotationCorrection);
        aiming.moveElevation(1.0);
    }

    public boolean isFinished()
    {
        return false;
    }

    private double calculateRotationCorrection()
    {
        double correction = 0.0;

        if (!visionAvailable())
        {
            rotationPID.reset();
            return correction;
        }

        final double error = vision.getYawError();
        final double sign = Math.signum(error);
        correction = rotationPID.update(sign);

        return correction;
    }

    private double calculateElevationCorrection()
    {
        double correction = 0.0;

        if (!visionAvailable())
        {
            elevationPID.reset();
            return correction;
        }

        final double error = vision.getPitchError();
        final double sign = Math.signum(error);
        correction = elevationPID.update(sign);

        return correction;
    }

    private boolean visionAvailable()
    {
        return vision.getModeTape() && vision.getInView();
    }
}
