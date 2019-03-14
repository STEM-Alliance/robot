package org.wfrobotics.robot.commands.system;

import org.wfrobotics.robot.commands.link.LinkToHeight;
import org.wfrobotics.robot.config.FieldHeight;
import org.wfrobotics.robot.config.IO;
import org.wfrobotics.robot.subsystems.SuperStructure;
import org.wfrobotics.robot.commands.elevator.ElevatorToHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;

public class SystemToLow extends ConditionalCommand
{
    private final SuperStructure superStructure = SuperStructure.getInstance();

    public SystemToLow()
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
            if (SuperStructure.getInstance().getHasCargo())
            {
                addParallel(new ElevatorToHeight(FieldHeight.CargoLow.getE()));
                addSequential(new LinkToHeight(FieldHeight.CargoLow.getL()));
            }
            else
            {
                addSequential(new SystemToHatch());
            }
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
                addSequential(new LinkToHeight(FieldHeight.HatchLow.getL()));
                addSequential(new ElevatorToHeight(FieldHeight.HatchLow.getE()));
        }
        // public boolean isFinished()
        // {
        //     return IO.getInstance().isLinkOverrideRequested() || IO.getInstance().isLinkOverrideRequested();
        // }
    }
}
