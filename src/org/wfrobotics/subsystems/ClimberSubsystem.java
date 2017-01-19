package org.wfrobotics.subsystems;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;

public class ClimberSubsystem extends Subsystem {

    @Override
    protected void initDefaultCommand()
    {
        // TODO Auto-generated method stub
    }

    /**
     * Control speed of the climbing spool
     * @param speed -1 (full down) to 1 (full up)
     */
    public void setSpeed(double speed)
    {
        DriverStation.reportError("Climber set speed not implemented yet", true);
    }
}
