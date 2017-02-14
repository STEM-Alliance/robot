package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Camera.TargetData;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class VisionGearDropOff extends CommandGroup 
{
    public VisionGearDropOff() 
    {
        TargetData data = Robot.targetingSubsystem.getData();

        if(data.InView)
        {
            double yawOffset = data.Yaw; //rotate control
            double tolerance = Constants.OPTIMAL_GEAR_DROP_OFF_DISTANCE * 0.05;

            addSequential(new AutoDrive(Constants.AUTONOMOUS_TURN_SPEED, yawOffset, Constants.AUTONOMOUS_TURN_TOLERANCE));

            //distance to boiler 

            if(Math.abs(Constants.OPTIMAL_GEAR_DROP_OFF_DISTANCE - Robot.targetingSubsystem.DistanceToTarget()) <= tolerance )
            {

                if(Robot.targetingSubsystem.DistanceToTarget() < Constants.OPTIMAL_GEAR_DROP_OFF_DISTANCE )                
                {
                    Robot.driveSubsystem.driveXY(0, .3, -1);            
                }
                else if(Robot.targetingSubsystem.DistanceToTarget() > Constants.OPTIMAL_GEAR_DROP_OFF_DISTANCE )
                {
                    Robot.driveSubsystem.driveXY(0, -.3, -1);            
                }
            }
        }
    }
    
    protected void execute()
    {
//        if(Robot.aligningSubsystem.getData().Yaw > 0) // too far to the right
//        {
//            Robot.driveSubsystem.driveXY(-0.2, 0, 0);                
//        }
//        else if(Robot.aligningSubsystem.getData().Yaw < 0)// too far to the left
//        {
//            Robot.driveSubsystem.driveXY(0.2, 0, 0);
//        }
//        else if(Robot.aligningSubsystem.getData().Yaw == 0)
//        {
//            if(Robot.targetingSubsystem.DistanceToTarget() > 0)
//            {
//                Robot.driveSubsystem.driveXY(0, 0.5, 0);
//            }
//            else if(Robot.targetingSubsystem.DistanceToTarget() == 0)
//            {
//                Robot.driveSubsystem.driveXY(0, 0, 0);
//            }
//        }
    }
}
