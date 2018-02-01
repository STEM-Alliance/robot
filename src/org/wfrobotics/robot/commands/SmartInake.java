package org.wfrobotics.robot.commands;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class SmartInake extends CommandGroup
{
    public SmartInake()
    {
        addParallel(new DistanceIntake());
        addParallel(new DistanceIntakeSolenoid());
    }
}
