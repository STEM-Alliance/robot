package org.wfrobotics.robot.path;

import org.wfrobotics.reuse.commands.SwitchChoice;
import org.wfrobotics.reuse.commands.drive.DriveDistance;
import org.wfrobotics.reuse.commands.drive.DrivePath;
import org.wfrobotics.reuse.commands.drive.TurnToHeading;
import org.wfrobotics.reuse.utilities.MatchState2018.Side;
import org.wfrobotics.robot.auto.IntakeSet;
import org.wfrobotics.robot.commands.intake.SmartIntake;
import org.wfrobotics.robot.commands.wrist.WristToHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class ModeCenter extends CommandGroup
{
    public ModeCenter()
    {
        addParallel(new WristToHeight(1.0));
        addSequential(new SwitchChoice(Side.Right, new DrivePath("CenterRight"), new DrivePath("CenterLeft")));
        addSequential(new IntakeSet(1.0, 0.5, true));  // Yes, 1.0 outtake is good here

        oneCube();
        //        twoCube();
    }

    public void oneCube()
    {
        // Get away from wall
        addSequential(new DriveDistance(-12.0 * 1.0));
        addParallel(new WristToHeight(0.0));
        addSequential(new SwitchChoice(Side.Right, new TurnToHeading(-90.0, 1.5), new TurnToHeading(90.0, 1.5)));
    }

    public void twoCube()
    {
        // Get away from wall
        addParallel(new WristToHeight(0.0));
        addSequential(new DriveDistance(-12.0 * 8.5));

        // Picking up secound cube
        addParallel(new SmartIntake());
        addSequential(new SwitchChoice(Side.Right, new DrivePath("CenterRightSecound"), new WaitCommand(3.0)));
        addSequential(new WaitCommand(.1));

        // Third path to the switch
        addSequential(new SwitchChoice(Side.Right, new DrivePath("CenterRightThird"), new WaitCommand(3.0)));
        addSequential(new IntakeSet(1.0, 0.5, true));
        addSequential(new SwitchChoice(Side.Right, new TurnToHeading(90.0, 2.0), new TurnToHeading(-90.0, 2.0)));
    }
}
