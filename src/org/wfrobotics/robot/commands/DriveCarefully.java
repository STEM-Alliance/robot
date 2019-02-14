package org.wfrobotics.robot.commands;

import org.wfrobotics.reuse.commands.drive.DriveCheesy;
import org.wfrobotics.reuse.math.Util;
import org.wfrobotics.robot.subsystems.Elevator;

public class DriveCarefully extends DriveCheesy
{
    private static final double kFast = 0.05;
    private static final double kSlow = 1.0;
    private static final double kElevatorBottom = 0.0;  // Inches
    private static final double kElevatorTop = 0.0;  // Inches

    private final Elevator elevator = Elevator.getInstance();

    @Override
    public void execute()
    {
        final double ramp = Util.scaleToRange(elevator.getPosition(), kElevatorBottom, kElevatorTop, kFast, kSlow);

        drive.setOpenLoopRampRate(ramp);
        super.execute();
    }

    @Override
    public void interrupted()
    {
        drive.setOpenLoopRampRate(kFast);
    }
}
