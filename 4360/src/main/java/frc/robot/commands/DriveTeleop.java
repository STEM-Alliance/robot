package frc.robot.commands;


import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.Config.RobotContainer;
import frc.robot.subsystems.Drivetrain;


/** Turn until reaching the target, or get to the expected heading it should be at **/
public final class DriveTeleop extends CommandBase
{

    private final Drivetrain drive;
    private final RobotContainer OI;
    private final double todd;
    // private final SimplePID pid = new SimplePID(0.025, 0.004);  // Tuned pretty good for coast mode
    // private final SimplePID pid = new SimplePID(0.16, 0.00001);  // Tuned well for brake mode

    public DriveTeleop( Drivetrain drive, RobotContainer OI,double todd)
    {
        addRequirements(drive);
        this.drive = drive;
        this.OI = OI;
        this.todd = todd;
    }

    public void initialize()
    {
     
    }

    public void execute()
    {
      if(todd == 1){
        drive.robotDrive.arcadeDrive(OI.xbox.getRawAxis(4) , OI.xbox.getRawAxis(1) , true);
      }
      if(todd == -1){
        drive.robotDrive.arcadeDrive(OI.xbox.getRawAxis(4)*-1 , OI.xbox.getRawAxis(1)*-1 , true);
      }
          
    }

    public boolean isFinished()
    {
        return false;
    }

   



  
}
