package org.wfrobotics.commands;

import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Targeting.TargetData;

import edu.wpi.first.wpilibj.command.CommandGroup;

/**
 *
 */
public class VisionShoot extends CommandGroup {

    public VisionShoot() {
        TargetData data = Robot.targetingSubsystem.getData();
        
        if (data.InView)
        {
            double pitchOffset = data.Pitch;
            // sample correct tape separation
            //get current tape separation
            // if current separation is less than correct separation
                //move closer (how much closer)
            //else if current separation is greater than correct separation
                //move farther (how much farther)
            //else
                //don't move
            //we need to calculate the time based on the data.pitchOffset
           
            // rotate the robot command thing here
            double yawOffset = data.Yaw;
            addSequential(new AutoDrive(yawOffset, .05 * yawOffset));
            //get correct tape point
            //get current tape point
            //using inverse sin find the angle based on the distance and offset of current and correct tape points
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
