package org.wfrobotics.robot.commands.intake;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class SmartOuttake extends  CommandGroup
{
    public SmartOuttake()
    {
        addParallel(new DistanceIntakePush());
        addParallel(new DistanceIntakeSolenoid(30));

    }
}
