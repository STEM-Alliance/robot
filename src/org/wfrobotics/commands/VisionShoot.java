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
            double yawOffset = data.Yaw;
            // rotate the robot command thing here
            addParallel(new AutoDrive(yawOffset, .05 * yawOffset));
            double pitchOffset = data.Pitch;
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
