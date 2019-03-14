package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.link.LinkToHeight;
import org.wfrobotics.robot.config.FieldHeight;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.SuperStructure;
import org.wfrobotics.robot.commands.elevator.ElevatorToHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class SystemToHigh extends ConditionalCommand
{
    private final SuperStructure superStructure = SuperStructure.getInstance();

    public SystemToHigh()
    {
        super(new SystemToCargo(), new SystemToHatch());
    }

    protected boolean condition()
    {
        return !superStructure.getHasHatch();
    }

    private static class SystemToCargo extends CommandGroup
    {
        public SystemToCargo()
        {
            addParallel(new ElevatorToHeight(FieldHeight.CargoHigh.getE()));
            addSequential(new LinkToHeight(FieldHeight.CargoHigh.getL()));
        }
        // public boolean isFinished()
        // {
        //     return IO.getInstance().isLinkOverrideRequested() || IO.getInstance().isLinkOverrideRequested();
        // }
    }

    private static class SystemToHatch extends CommandGroup
    {
        public SystemToHatch()
        {
            addParallel(new ElevatorToHeight(FieldHeight.HatchHigh.getE()));
            addSequential(new LinkToHeight(FieldHeight.HatchHigh.getL()));
        }
        // public boolean isFinished()
        // {
        //     return IO.getInstance().isLinkOverrideRequested() || IO.getInstance().isLinkOverrideRequested();
        // }
    }
}