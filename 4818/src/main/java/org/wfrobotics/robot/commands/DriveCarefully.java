package org.wfrobotics.robot.commands;

import org.wfrobotics.reuse.commands.drive.DriveCheesy;
import org.wfrobotics.reuse.math.Util;
import org.wfrobotics.robot.config.LiftHeight;
import org.wfrobotics.robot.subsystems.Lift;

public class DriveCarefully extends DriveCheesy
{
    private static final double kFast = 0.05;
    private static final double kSlow = 1.0;
    private final Lift lift = Lift.getInstance();

    @Override
    public void execute()
    {
        final double ramp = Util.scaleToRange(lift.getPosition(), LiftHeight.Intake.get(), LiftHeight.Scale.get(), kFast, kSlow);

        drive.setOpenLoopRampRate(ramp);
        super.execute();
    }

    @Override
    public void interrupted()
    {
        drive.setOpenLoopRampRate(kFast);
    }
}
