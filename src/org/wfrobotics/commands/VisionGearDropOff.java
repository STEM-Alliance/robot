package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Camera.TargetData;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class VisionGearDropOff extends CommandGroup 
{
    enum STATE {YAW, APPROACH, SPRING, VICTORY};
    STATE state;
    //comment
    TargetData data;
    double tolerance;
    
    public VisionGearDropOff() 
    {
        data = Robot.targetingSubsystem.getData();
        state = STATE.YAW;

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
        switch(state)
        {
            case YAW:
               if(data.Yaw > 0) //too far to the right
               {
                   addSequential( new AutoDrive(-.25, 0, 0, 0, 1));
               }else if(data.Yaw < 0) // too far to the left
               {
                   addSequential( new AutoDrive(.25, 0, 0, 0, 1));
               }else if(data.Yaw == 0)
               {
                   state = STATE.APPROACH;   
               }
                break;
            case APPROACH:
                if(data.Depth == 0 | Robot.driveSubsystem.isSpringInGear())
                {
                    state = STATE.SPRING;
                }
               else if(data.Depth > 0)
               {
                   addSequential( new AutoDrive(0, .25, 0, 0, .5));
               }
                break;
            case SPRING:
                if(!Robot.driveSubsystem.isGearStored())
                {
                    state = STATE.VICTORY;
                }
                break;
            case VICTORY:
                addSequential( new AutoDrive(0, -.5, 0, 0, 1));
                isFinished();
                break;
        }
    }
}
