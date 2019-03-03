package org.wfrobotics.robot;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.hardware.Blinkin;
import org.wfrobotics.reuse.hardware.lowleveldriver.BlinkinPatterns.PatternName;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.config.MatchState2018;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.Lift;
import org.wfrobotics.robot.subsystems.Winch;
import org.wfrobotics.robot.subsystems.Wrist;

/**
 * Robot: Victor - 2018
 * @author Team 4818 The Herd<p>STEM Alliance of Fargo Moorhead
 * */
public final class Robot extends EnhancedRobot
{
    static
    {
        EnhancedRobot.config = RobotConfig.getInstance();
    }

    public Robot ()
    {
        super(RobotConfig.getInstance(), RobotState.getInstance(), IO.getInstance());
    }

    protected void registerRobotSpecific()
    {
        EnhancedRobot.leds =  new Blinkin(9, PatternName.Yellow);
        subsystems.register(Intake.getInstance());
        subsystems.register(Lift.getInstance());
        subsystems.register(Winch.getInstance());
        subsystems.register(Wrist.getInstance());
    }

    @Override
    public void disabledPeriodic()
    {
        MatchState2018.getInstance().update();  // Need game-specific data
        super.disabledPeriodic();
    }
}
