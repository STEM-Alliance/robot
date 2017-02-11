package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Targeting.TargetData;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class VisionGearDropOff extends CommandGroup {

    public VisionGearDropOff() {
        TargetData data = Robot.targetingSubsystem.getData(false);
        
    if(data.InView)
    {
        double yawOffset = data.Yaw; //rotate control
        double tolerance = Constants.OPTIMAL_GEAR_DROP_OFF_DISTANCE * .05;

        addSequential(new AutoDrive(Constants.AUTONOMOUS_TURN_SPEED, yawOffset, Constants.AUTONOMOUS_TURN_TOLERANCE));

        //distance to boiler 

        if(Math.abs(Constants.OPTIMAL_GEAR_DROP_OFF_DISTANCE - Robot.targetingSubsystem.DistanceToTarget(false)) <= tolerance )
        {

            if(Robot.targetingSubsystem.DistanceToTarget(false) < Constants.OPTIMAL_GEAR_DROP_OFF_DISTANCE )                
            {
                Robot.driveSubsystem.driveXY(0, .3, -1);            
            }
            else if(Robot.targetingSubsystem.DistanceToTarget(false) > Constants.OPTIMAL_GEAR_DROP_OFF_DISTANCE )
            {
                Robot.driveSubsystem.driveXY(0, -.3, -1);            
            }
        // Add Commands here:
        // e.g. addSequential(new Command1());
        //      addSequential(new Command2());
        // these will run in order.

        // To run multiple commands at the same time,
        // use addParallel()
        // e.g. addParallel(new Command1());
        //      addSequential(new Command2());
        // Command1 and Command2 will run in parallel.

        // A command group will require all of the subsystems that each member
        // would require.
        // e.g. if Command1 requires chassis, and Command2 requires arm,
        // a CommandGroup containing them would require both the chassis and the
        // arm.
        }
    }
    }
}
