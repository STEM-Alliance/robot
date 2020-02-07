package frc.robot.commands; 


import edu.wpi.first.wpilibj2.command.CommandBase;
import frc.robot.subsystems.Aiming;

/**
 *
 */
public class StaticAim extends CommandBase {

    private Aiming aiming;

    public StaticAim(Aiming aiming) {
        addRequirements(aiming);
        this.aiming = aiming;

    }

    // Called just before this Command runs the first time
    @Override
    public void initialize() {
    }

    // Called repeatedly when this Command is scheduled to run
    @Override
    public void execute() {
    }

    // Make this return true when this Command no longer needs to run execute()
    @Override
    public boolean isFinished() {
        return false; 
    }

}
