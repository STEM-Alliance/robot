package org.wfrobotics.prototype.commands;

import org.wfrobotics.prototype.Robot;

import edu.wpi.first.wpilibj.command.Command;

public class DriveRocketLeague extends Command {
    double speedL;
    double speedR;
    double LeftX;
    double TriggerRotation;

    public DriveRocketLeague()
    {
        requires(Robot.tankSubsystem);
        // TODO Auto-generated constructor stub
    }

    public DriveRocketLeague(String name)
    {
        super(name);
        // TODO Auto-generated constructor stub
    }

    public void execute()
    {
        this.LeftX = Robot.controls.getLeftX();
        this.TriggerRotation = Robot.controls.getTriggerRotationDriver();

        LeftX = Math.signum(LeftX) * LeftX * LeftX;

        if(!(LeftX == 0))
        {
//            if(TriggerRotation < 0)
//            {
//                speedL =  (TriggerRotation - LeftX);
//                speedR =  (TriggerRotation + LeftX);
//            }
//            else
          //  {
                speedL =  (TriggerRotation + LeftX);
                speedR =  (TriggerRotation - LeftX);
           // }

         Robot.tankSubsystem.setSpeedRight(speedR);
         Robot.tankSubsystem.setSpeedLeft(speedL);
        }
        else
        {
            speedL =  TriggerRotation;
            speedR =  TriggerRotation;

        Robot.tankSubsystem.setSpeedLeft(speedL);
        Robot.tankSubsystem.setSpeedRight(speedR);
        }
    }

    @Override
    protected boolean isFinished()
    {
        // TODO Auto-generated method stub
        return false;
    }

}
