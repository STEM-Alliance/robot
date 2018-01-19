package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveTank extends Command {

    public double speedL;
    public double speedR;

    public DriveTank ()
    {
        requires(Robot.tankSubsystem);
        // TODO Auto-generated constructor stub
    }

    public DriveTank(double speed)
    {
        Robot.tankSubsystem.setSpeedLeft(speed);
        Robot.tankSubsystem.setSpeedRight(speed);
    }

    public void execute()
    {
     this.speedL = Robot.controls.getLeftY();
     Robot.tankSubsystem.setSpeedLeft(speedL);

     this.speedR = Robot.controls.getRightY();
     Robot.tankSubsystem.setSpeedRight(speedR);

    }

    @Override
    protected boolean isFinished()
    {
        // TODO Auto-generated method stub
        return false;
    }

}
