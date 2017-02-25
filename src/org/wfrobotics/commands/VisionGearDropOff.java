package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Camera.TargetData;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class VisionGearDropOff extends CommandGroup {
    enum STATE {
        YAW, APPROACH, SPRING
    };

    STATE state;
    // comment
    double tolerance;

    public VisionGearDropOff()
    {
        Robot.targetGearSubsystem.run();

        /*
         * if(data.InView) { double yawOffset = data.Yaw; //rotate control
         * //double tolerance = Constants.OPTIMAL_GEAR_DROP_OFF_DISTANCE * 0.05;
         * 
         * addSequential(new AutoDrive(Constants.AUTONOMOUS_TURN_SPEED,
         * yawOffset, Constants.AUTONOMOUS_TURN_TOLERANCE));
         * 
         * //distance to boiler
         * 
         * if(Math.abs(Constants.OPTIMAL_GEAR_DROP_OFF_DISTANCE -
         * Robot.targetingSubsystem.DistanceToTarget()) <= tolerance ) {
         * 
         * if(Robot.targetingSubsystem.DistanceToTarget() <
         * Constants.OPTIMAL_GEAR_DROP_OFF_DISTANCE ) {
         * Robot.driveSubsystem.driveXY(0, .3, -1); } else
         * if(Robot.targetingSubsystem.DistanceToTarget() >
         * Constants.OPTIMAL_GEAR_DROP_OFF_DISTANCE ) {
         * Robot.driveSubsystem.driveXY(0, -.3, -1); } } }
         */
    }

    protected void execute()
    {
        double xDistance = Robot.targetGearSubsystem.DistanceFromCenter;
        switch (state)
        {
            case YAW:
                
                if(Robot.targetGearSubsystem.InView)
                {
                    if (xDistance > 0.1) // too far to the right
                    {
                        addSequential(new AutoDrive(-.25, 0, 0, 0, 1));
                    }
                    else if (xDistance < 0.1) // too far to the left
                    {
                        addSequential(new AutoDrive(.25, 0, 0, 0, 1));
                    }
                    else
                    {
                        state = STATE.APPROACH;
                    }
                }
                break;
            case APPROACH:
                addSequential(new AutoDrive(0, .25, 0, 0, .5));
                // if(data.Depth == 0 | Robot.driveSubsystem.isSpringInGear())
                // {
                // state = STATE.SPRING;
                // }
                // else if(data.Depth > 0)
                // {
                // addSequential( new AutoDrive(0, .25, 0, 0, .5));
                // }
                break;
            case SPRING:
                if (!Robot.driveSubsystem.isGearStored())
                {
                    isFinished();
                }
                break;
        }
    }
}
