package org.wfrobotics.commands.drive.cal;

import org.wfrobotics.hardware.cal.SpeedPIDFCommand;
import org.wfrobotics.robot.Robot;

public class WheelDrive extends SpeedPIDFCommand
{
    public interface AutoCalibratable
    {
        public void setTalonCal(int mode, double setpoint);
    }
    
    public WheelDrive()
    {
        super(1, 4);
        this.requires(Robot.driveSubsystem);
    }

    @Override
    public void setMotor(int mode, double raw)
    {
        Robot.driveSubsystem.setTalonCal(mode, raw);
    }
}
