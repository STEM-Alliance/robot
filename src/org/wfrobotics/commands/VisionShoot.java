package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Targeting.TargetData;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class VisionShoot extends CommandGroup 
{
    public VisionShoot() 
    {
        TargetData data = Robot.targetingSubsystem.getData();

        if (data.InView)
        {
            double yawOffset = data.Yaw; //rotate control
            double tolerance = Constants.OPTIMAL_SHOOTING_DISTANCE * .05;

            addSequential(new AutoDrive(Constants.AUTONOMOUS_TURN_SPEED, yawOffset, Constants.AUTONOMOUS_TURN_TOLERANCE));

            //distance to boiler 

            if(Math.abs(Constants.OPTIMAL_SHOOTING_DISTANCE - Robot.targetingSubsystem.DistanceToTarget()) <= tolerance )
            {

                if(Robot.targetingSubsystem.DistanceToTarget() < Constants.OPTIMAL_SHOOTING_DISTANCE )                
                {
                    Robot.driveSubsystem.driveXY(0, .3, -1);            
                }
                else if(Robot.targetingSubsystem.DistanceToTarget() > Constants.OPTIMAL_SHOOTING_DISTANCE )
                {
                    Robot.driveSubsystem.driveXY(0, -.3, -1);            
                }

                //check coordinates (tolerances)

                //check distance (tolerances) 
                //if not pointing at boiler
                //turn towards the boiler
                //if not at correct distance
                //go to correct distance
                //verify coordinates

                //shoot
            }
        }
    }
}
