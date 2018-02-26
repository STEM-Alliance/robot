package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.tank.TankSubsystem;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.robot.commands.drive.DriveCheesy;
import org.wfrobotics.robot.config.robotConfigs.RobotConfig;

public class DriveSubsystem extends TankSubsystem
{
    private static DriveSubsystem instance = null;

    public DriveSubsystem(RobotConfig config)
    {
        super(config);
    }

    public static DriveSubsystem getInstance()
    {
        if (instance == null) { instance = new DriveSubsystem(Robot.config); }
        return instance;
    }

    public void initDefaultCommand()
    {
        setDefaultCommand(new DriveCheesy());
    }
}
