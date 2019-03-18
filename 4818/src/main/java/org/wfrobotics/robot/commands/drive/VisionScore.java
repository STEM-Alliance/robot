package org.wfrobotics.robot.commands.drive;

import org.wfrobotics.reuse.commands.drive.DriveOpenLoop;
import org.wfrobotics.robot.commands.intake.ScoreGamepiece;

import edu.wpi.first.wpilibj.command.CommandGroup;

/** Turn until reaching the target, or get to the expected heading it should be at **/
public class VisionScore extends CommandGroup
{
    private static final double kThrottleApproachMax = 0.4;
    private static final double kThrottleBackUp = -kThrottleApproachMax;
    private static final double kBackUpSeconds = 0.4;

    public VisionScore()
    {
        this.addSequential(new VisionDeploy(kThrottleApproachMax));
        this.addParallel(new ScoreGamepiece());  // Pretending to score cargo
        this.addSequential(new DriveOpenLoop(kThrottleBackUp), kBackUpSeconds);
    }
}
