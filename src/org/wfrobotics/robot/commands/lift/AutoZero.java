package org.wfrobotics.robot.commands.lift;

import org.wfrobotics.robot.Robot;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.ConditionalCommand;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class AutoZero extends CommandGroup
{
    private class ConditionalDown extends ConditionalCommand
    {
        public ConditionalDown()
        {
            super(new LiftGoHome(-0.2, 15.0));
        }

        protected void initialize()
        {
            SmartDashboard.putString("Lift", getClass().getSimpleName());
        }

        protected boolean condition()
        {
            return !LiftGoHome.everZeroed();
        }
    }

    public AutoZero()
    {
        this.addSequential(new LiftGoHome(0.4, 0.5));
        this.addSequential(new ConditionalDown());
    }

    protected void initialize()
    {
        SmartDashboard.putString("Lift", getClass().getSimpleName());
    }

    protected void end()
    {
        Robot.liftSubsystem.goToSpeedInit(0);
    }
}
