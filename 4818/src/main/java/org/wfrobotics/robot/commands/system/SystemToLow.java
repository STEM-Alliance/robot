package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.link.LinkToHeight;
import org.wfrobotics.robot.subsystems.Intake;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class SystemToLow extends ConditionalCommand
{
    private final Intake intake = Intake.getInstance();

    public SystemToLow()
    {
        super(new SystemToCargo(), new SystemToHatch());
    }

    protected boolean condition()
    {
        return !intake.hasHatch();
    }

    private static class SystemToCargo extends CommandGroup
    {
        public SystemToCargo()
        {
            //        addParallel(new ElevatorToHeight(0.0));
            addSequential(new LinkToHeight(40.0));
        }
    }

    private static class SystemToHatch extends CommandGroup
    {
        public SystemToHatch()
        {
            //        addParallel(new ElevatorToHeight(0.0));
            addSequential(new LinkToHeight(21.0));
        }
    }
}
