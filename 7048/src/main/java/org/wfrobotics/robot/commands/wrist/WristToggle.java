package org.wfrobotics.robot.commands.wrist;

import org.wfrobotics.reuse.subsystems.PositionBasedSubsystem.PositionConfig;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.Wrist;

import edu.wpi.first.wpilibj.command.InstantCommand;

public class WristToggle extends InstantCommand
{
    private final double kCargoModeDegrees;
    private final double kHatchModeDegrees;
    private static boolean inCargoMode = false;  // TODO This is not the best way to do this

    private final Wrist wrist = Wrist.getInstance();

    public WristToggle()
    {
        final PositionConfig config = RobotConfig.getInstance().getWristConfig();

        requires(wrist);
        kHatchModeDegrees = config.kFullRangeInchesOrDegrees;
        kCargoModeDegrees = 0.0;
    }

    protected void initialize()
    {
        final boolean goToCargo = !inCargoMode;
        final double setpoint = (goToCargo) ? kCargoModeDegrees : kHatchModeDegrees;

        wrist.setClosedLoop(setpoint);
        inCargoMode = !inCargoMode;
    }
}
