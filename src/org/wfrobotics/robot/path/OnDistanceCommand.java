package org.wfrobotics.robot.path;

import org.wfrobotics.robot.RobotState;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class OnDistanceCommand extends CommandGroup
{
    public OnDistanceCommand(Command cmd, double inches, double inchesTol)
    {
        this.addSequential(new Wait(inches, inchesTol));
        this.addSequential(cmd);
    }

    private class Wait extends Command
    {
        private final RobotState state = RobotState.getInstance();
        private final double inches;
        private final double tol;

        public Wait(double inches, double inchesTol)
        {
            this.inches = inches;
            tol = inchesTol;
        }

        protected boolean isFinished()
        {
            return Math.abs(state.robotDistanceDriven - inches) < tol;
        }
    }
}
