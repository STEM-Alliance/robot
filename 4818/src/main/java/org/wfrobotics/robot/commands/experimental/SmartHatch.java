package org.wfrobotics.robot.commands.experimental;

import org.wfrobotics.robot.subsystems.Intake;
import org.wfrobotics.robot.subsystems.SuperStructure;

import edu.wpi.first.wpilibj.command.Command;
import edu.wpi.first.wpilibj.command.CommandGroup;

public class SmartHatch extends CommandGroup
{
    public SmartHatch()
    {
        this.addSequential(new SmartHatchIntake());
        this.addSequential(new SmartHatchOuttake());
    }

    private class SmartHatchIntake extends Command
    {
        private final Intake intake = Intake.getInstance();
        private final SuperStructure ss = SuperStructure.getInstance();

        public SmartHatchIntake()
        {
            requires(intake);
        }

        protected void initialize()
        {
            final boolean needHatch = !intake.getGrabbersExtended();
            intake.setGrabber(needHatch);
            intake.setCargoSpeed(0.0);
        }

        protected boolean isFinished()
        {
            final boolean gotHatch = ss.getHasHatch();
            return gotHatch;  // Finish if we got a hatch or the operator releases button to cancel
        }

        protected void end()
        {
            intake.setGrabber(false);
        }
    }

    private class SmartHatchOuttake extends Command
    {
        private final Intake intake = Intake.getInstance();

        public SmartHatchOuttake()
        {
            requires(intake);
        }

        protected boolean isFinished()
        {
            return false;
        }

        protected void end()
        {
            intake.setGrabber(true);  // Shoot hatch (or nothing) when button is finally released
        }
    }
}
