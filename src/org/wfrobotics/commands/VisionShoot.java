package org.wfrobotics.commands;

import org.wfrobotics.Vector;
import org.wfrobotics.robot.Robot;
import org.wfrobotics.subsystems.Camera.TargetData;

import edu.wpi.first.wpilibj.command.CommandGroup;

public class VisionShoot extends CommandGroup 
{   
    private TargetData data;
    private Shoot shoot;
    
    public VisionShoot() 
    {
     
        requires (Robot.driveSubsystem);
        this.data = Robot.targetingSubsystem.getData();
        shoot = new Shoot(Conveyer.MODE.OFF);
        addSequential(shoot);
    }
    protected void execute()
    {
        if (data.InView)
        {
            double yawOffset = data.Yaw; //rotate control
            Vector vector = new Vector(.5, .5); 
            Robot.driveSubsystem.driveWithHeading(vector, .5, yawOffset);
            addSequential(new AutoDrive(Constants.AUTONOMOUS_TURN_SPEED, yawOffset, Constants.AUTONOMOUS_TURN_TOLERANCE));

            //distance to boiler 
            double tolerance = Constants.OPTIMAL_SHOOTING_DISTANCE * .05;
            if(Math.abs(Constants.OPTIMAL_SHOOTING_DISTANCE - 
                   Robot.targetingSubsystem.DistanceToTarget()) <= tolerance &&
                   Math.abs(Constants.OPTIMAL_SHOOTING_DISTANCE - 
                           Robot.targetingSubsystem.DistanceToTarget()) >= tolerance)
            {
            
                shoot = new Shoot(Conveyer.MODE.OFF);

              //TODO Use a PID loop here if this isn't good enough
                //speed = GetPIDSpeed(error, )
                 //error = desired-actual
                 //public double GetPIDSpeed(error)
//                 {
//                return 5;
//                 }
                if(Robot.targetingSubsystem.DistanceToTarget() < Constants.OPTIMAL_SHOOTING_DISTANCE)
                {
                    Robot.driveSubsystem.driveXY(0, .3, 0);            
                }
                else if(Robot.targetingSubsystem.DistanceToTarget() > Constants.OPTIMAL_SHOOTING_DISTANCE)
                {
                    Robot.driveSubsystem.driveXY(0, -.3, 0);            
                }
            else
            {
                //addSequential(new Shoot(Conveyer.MODE.CONTINUOUS));
                shoot = new Shoot(Conveyer.MODE.CONTINUOUS);
            }
        }
        else
        {
            //TODO: Turn until you see the boiler
        }
       
    }
    }
}
                //check coordinates (tolerances)

                //check distance (tolerances) 
                //if not pointing at boiler
                //turn towards the boiler
                //if not at correct distance
                //go to correct distance
                //verify coordinates

                //shoot
    


