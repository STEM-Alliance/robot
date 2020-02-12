package frc.robot.commands;

import edu.wpi.first.wpilibj.Preferences;
import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Config.RobotContainer;
import frc.robot.subsystems.Drivetrain;
import frc.robot.subsystems.Vision;

/** Turn until reaching the target, or get to the expected heading it should be at **/
public final class DriveTeleop extends CommandBase
{

    private final Drivetrain drive;
    private final RobotContainer OI;
 
    // private final SimplePID pid = new SimplePID(0.025, 0.004);  // Tuned pretty good for coast mode
    // private final SimplePID pid = new SimplePID(0.16, 0.00001);  // Tuned well for brake mode

    public DriveTeleop( Drivetrain drive, RobotContainer OI)
    {
        addRequirements(drive);
        this.drive = drive;
        this.OI = OI;
    }

    public void initialize()
    {
     
    }

    public void execute()
    {
      
        drive.robotDrive.tankDrive(OI.xbox.getRawAxis(1) *.65, OI.xbox.getRawAxis(5) * .65, true);
        }

    public boolean isFinished()
    {
        return false;
    }

   



  
}
