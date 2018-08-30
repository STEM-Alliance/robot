package org.wfrobotics.robot.auto;

import org.wfrobotics.reuse.commands.drive.DriveDistance;
import org.wfrobotics.reuse.commands.drive.DrivePathPosition;
import org.wfrobotics.reuse.commands.drive.TurnToHeading;
import org.wfrobotics.robot.auto.MatchState2018.Side;
import org.wfrobotics.robot.commands.intake.SmartIntake;
import org.wfrobotics.robot.commands.wrist.WristToHeight;

import edu.wpi.first.wpilibj.command.CommandGroup;
import edu.wpi.first.wpilibj.command.WaitCommand;

public class ModeCenter extends CommandGroup
{
    public ModeCenter()
    {
        addParallel(new WristToHeight(90.0));
        addSequential(new SwitchChoice(Side.Right, new DrivePathPosition("CenterRight"), new DrivePathPosition("CenterLeft")));
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
        addSequential(new SwitchChoice(Side.Right, new DrivePathPosition("CenterRightSecound"), new WaitCommand(3.0)));
        addSequential(new WaitCommand(.1));

        // Third path to the switch
        addSequential(new SwitchChoice(Side.Right, new DrivePathPosition("CenterRightThird"), new WaitCommand(3.0)));
        addSequential(new IntakeSet(1.0, 0.5, true));
        addSequential(new SwitchChoice(Side.Right, new TurnToHeading(90.0, 2.0), new TurnToHeading(-90.0, 2.0)));
    }
}
