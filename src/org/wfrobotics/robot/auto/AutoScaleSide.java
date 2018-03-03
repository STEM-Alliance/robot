package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.ScaleChoice;
import org.wfrobotics.reuse.commands.drivebasic.AutoDrive;
import org.wfrobotics.reuse.commands.drivebasic.DriveDistance;
import org.wfrobotics.reuse.commands.drivebasic.TurnToHeading;
import org.wfrobotics.reuse.utilities.HerdVector;
import org.wfrobotics.robot.commands.intake.WristToggle;
import org.wfrobotics.robot.commands.lift.LiftToHeight;
import org.wfrobotics.robot.config.Autonomous.POSITION;
import org.wfrobotics.robot.config.LiftHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class AutoScaleSide extends CommandGroup
{
    public AutoScaleSide(POSITION location)
    {
        addSequential(new DriveDistance(12.0 * 23.75 + 0.0));

        if (location == POSITION.RIGHT)
        {
            addSequential(new ScaleChoice(new ScoreScale(true)));
        }
        else
        {
            addSequential(new ScaleChoice(new ScoreScale(false)));
        }
    }
    private class ScoreScale extends CommandGroup
    {
        public ScoreScale(boolean flipAngle)
        {
            addParallel(new LiftToHeight(LiftHeight.Scale.get()));
            addSequential(new TurnToHeading((flipAngle) ? -90.0 : 90.0, 5.0));
            addSequential(new TurnToHeading((flipAngle) ? -90.0 : 90.0, 5.0));
            addSequential(new AutoDrive(new HerdVector(0.5, (flipAngle) ? -90.0 : 90.0), 0.5));
            addSequential(new WristToggle());
            addSequential(new IntakeSet( 0.4, 0.5, true));
            addSequential(new DriveDistance(12.0 * -3.0));
        }
    }

}
