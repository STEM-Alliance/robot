package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveArcade extends Command {

    double forward;
    double turn;
    double speedL;
    double speedR;

    public DriveArcade()
    {
        requires(Robot.tankSubsystem);
        // TODO Auto-generated constructor stub
    }

    public void execute()
    {
        this.forward = Robot.controls.getLeftY();
        this.turn = Robot.controls.getLeftX();

        speedL =  (forward + turn);
        speedR =  (forward - turn);

        Robot.tankSubsystem.setSpeedRight(speedR);
        Robot.tankSubsystem.setSpeedLeft(speedL);

    }

    @Override
    protected boolean isFinished()
    {
        // TODO Auto-generated method stub
        return false;
    }

}
