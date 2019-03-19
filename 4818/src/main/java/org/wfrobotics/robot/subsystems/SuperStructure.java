package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.SuperStructureBase;
import org.wfrobotics.robot.commands.ConserveCompressor;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;

public class SuperStructure extends SuperStructureBase
{
    static class SingletonHolder
    {
        static SuperStructure instance = new SuperStructure();
    }

    public static SuperStructure getInstance()
    {
        return SingletonHolder.instance;
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new ConserveCompressor());
    }

    final NetworkTableInstance netInstance = NetworkTableInstance.getDefault();
    final NetworkTable chickenVision = netInstance.getTable("ChickenVision");

    private boolean driverVision, 
                    tapeVision, 
                    cargoVision, 
                    cargoSeen, 
                    tapeSeen;
    private NetworkTableEntry tapeDetected, 
                              cargoDetected,    
                              tapeYaw, 
                              cargoYaw,
                              videoTimestamp;

    public boolean getTapeInView()  {   return chickenVision.getEntry("tapeDetected").getBoolean(false);    }
    public boolean getCargoInView() {   return chickenVision.getEntry("cargoDetected").getBoolean(false);   }
    public double getTapeYaw()  {   return chickenVision.getEntry("tapeYaw").getDouble(0.0);    }
    public double getCargoYaw() {   return chickenVision.getEntry("cargoYaw").getDouble(0.0);   }
    public boolean getDriveCamera() {   return chickenVision.getEntry("Driver").getBoolean(false);  }
    public boolean getTapeCamera()  {   return chickenVision.getEntry("Tape").getBoolean(false);    }
    public boolean getCargoCamera() {   return chickenVision.getEntry("Cargo").getBoolean(false);   }
    public double getLastTimestamp(){   return chickenVision.getEntry("VideoTimestamp").getDouble(0.0); }
}
