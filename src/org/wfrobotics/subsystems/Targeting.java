package org.wfrobotics.subsystems;

import org.wfrobotics.vision.Constants;

import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.networktables.NetworkTablesJNI;

/**
 * Provides information used to shoot the ball.
 * This subsystem translates pictures into data that commands can use to correct how they are aiming.
 *
 */
public class Targeting extends Subsystem 
{   
    public class TargetData 
    {
        public double Yaw = 20; // x 
        public double Pitch = 20; // y 
        public double Depth = 20;// z
        public boolean InView = true;
    }

    NetworkTable table;
    TargetData dataShooter;
    TargetData dataGear;
    
    @Override
    protected void initDefaultCommand()
    {
        // TODO set a commmand IF this remains a subsystem
    }
    
    public Targeting()
    {
        dataShooter = new TargetData();
        dataGear = new TargetData();
        table = NetworkTable.getTable("GRIP/GearTarget");
        table = NetworkTable.getTable("GRIP/ShooterTarget");
    }
    
    public TargetData getData(boolean isShooter)
    {
        
        dataGear.Yaw = table.getNumber("yaw", 0);
        dataGear.Pitch = table.getNumber("pitch", 0);
        dataGear.Depth = table.getNumber("depth", 0);
        dataGear.InView = table.getBoolean("yaw", false);

        dataShooter.Yaw = table.getNumber("yaw", 0);
        dataShooter.Pitch = table.getNumber("pitch", 0);
        dataShooter.Depth = table.getNumber("depth", 0);
        dataShooter.InView = table.getBoolean("yaw", false);

        // TODO get the data from the pi
        if(isShooter)
        {
            return dataShooter;
        }
        else
        {
            return dataGear;
        }
    }   
    // TODO remove this after we can get info from the pi
    public void testIncrementData(double yawOffset, double pitchOffset)
    {
        dataShooter.Yaw += yawOffset;
        dataShooter.Pitch += pitchOffset;
    }
    
    public double DistanceToTarget(boolean isShooter)
    {
        if(isShooter)
        {
        return (Constants.TargetHeightIn * Constants.FocalLengthIn) / dataShooter.Pitch;
        }
        else
            return (Constants.TargetHeightIn * Constants.FocalLengthIn) / dataGear.Pitch;
        }
}