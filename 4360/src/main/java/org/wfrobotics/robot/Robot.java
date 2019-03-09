package org.wfrobotics.robot;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.utilities.DashboardView;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.IntakeMotor;
import org.wfrobotics.robot.subsystems.Lift;
import org.wfrobotics.robot.subsystems.ParallelLink;

public final class Robot extends EnhancedRobot
{
    public Robot()
    {
        super( RobotConfig.getInstance(), RobotState.getInstance(), IO.getInstance());
    }

    protected void registerRobotSpecific()
    {
		subsystems.register(Intake.getInstance());
		subsystems.register(Lift.getInstance());
	    subsystems.register(IntakeMotor.getInstance());
		subsystems.register(ParallelLink.getInstance());
		DashboardView.startPerformanceCamera();
    }
}