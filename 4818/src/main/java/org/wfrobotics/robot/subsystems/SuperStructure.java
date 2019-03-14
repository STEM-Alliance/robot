package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.Canifier;
import org.wfrobotics.reuse.hardware.Canifier.RGB;
import org.wfrobotics.reuse.hardware.sensors.SharpDistance;
import org.wfrobotics.reuse.subsystems.SuperStructureBase;
import org.wfrobotics.robot.commands.ConserveCompressor;
import org.wfrobotics.robot.config.RobotConfig;
import org.wfrobotics.robot.subsystems.SuperStructure.CachedIO;
import org.wfrobotics.robot.subsystems.SuperStructure.SingletonHolder;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SuperStructure extends SuperStructureBase
{   
    private final Canifier jeff = new Canifier(6, new RGB(255, 255, 0));

    private final SharpDistance distanceL = new SharpDistance(1);
    private final SharpDistance distanceR = new SharpDistance(2);

    private final CachedIO cachedIO = new CachedIO();

    public Canifier getJeff()
    {
        return jeff;
    }

    static class SingletonHolder
    {
        static SuperStructure instance = new SuperStructure();
    }

    public static SuperStructure getInstance()
    {
        return SingletonHolder.instance;
    }
    
    public void cacheSensors(boolean isDisabled)
    {
        cachedIO.hasHatch = jeff.getLimitSwitchF();
        cachedIO.cargoLeft = jeff.getPWM0();
        cachedIO.cargoRight = jeff.getPWM1();
        cachedIO.distanceLeft = distanceL.getDistanceInches();
        cachedIO.distanceRight = distanceR.getDistanceInches();
    }

    public void reportState()
    {
        SmartDashboard.putBoolean("hasHatch", cachedIO.hasHatch);
        SmartDashboard.putBoolean("Reported Hatch", getHasHatch());
        SmartDashboard.putBoolean("Storing hatch", storedHatch);

        SmartDashboard.putNumber("Tape Vision Angle", getTapeYaw());
        SmartDashboard.putBoolean("Tape In view", getTapeInView());



        SmartDashboard.putBoolean("Cargo In", getHasCargo());
        SmartDashboard.putBoolean("Cargo L", cachedIO.cargoLeft);
        

        SmartDashboard.putNumber("Distance L", cachedIO.distanceLeft);
        SmartDashboard.putNumber("Distance R", cachedIO.distanceRight);
        SmartDashboard.putNumber("Distance From Wall", getDistanceFromWall());
        SmartDashboard.putNumber("Angle From Wall", getAngleFromWall());
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new ConserveCompressor());
    }
    
    protected static class CachedIO
    {
        public boolean hasHatch;
        public boolean cargoRight;
        public boolean cargoLeft;
        public double distanceLeft;
        public double distanceRight;
    }
    private boolean storedHatch = false;
    public void setStoreHatch(boolean storing)
    {
        storedHatch = storing;
    }
    public boolean getHasHatch()
    {
        return cachedIO.hasHatch || storedHatch;
    }
    boolean lastCargo = false;
    double cargoTimeout = 0;
    
    public boolean getHasCargo()
    {
        if (cachedIO.cargoRight || cachedIO.cargoLeft)
        {
            if(lastCargo == false)
            {
                cargoTimeout = Timer.getFPGATimestamp();
            }
            lastCargo = true;
        } else{ lastCargo = false; }

        boolean output = false;
        if(lastCargo){
            if ((Timer.getFPGATimestamp() - cargoTimeout) >= 0.05)
            {
                output = true;
            }
        }
        return output;
    }

    public double getAngleFromWall() {
        return Math.toDegrees(Math.atan((cachedIO.distanceLeft - cachedIO.distanceRight)/15.0));
    }

	public double getDistanceFromWall() {
		return (cachedIO.distanceLeft + cachedIO.distanceRight) / 2.0;
    }

    NetworkTableInstance netInstance = NetworkTableInstance.getDefault();
    NetworkTable chickenVision = netInstance.getTable("ChickenVision");

    private boolean driverVision, tapeVision, cargoVision, cargoSeen, tapeSeen;
    private NetworkTableEntry tapeDetected, cargoDetected, tapeYaw, cargoYaw,
        videoTimestamp;

    public boolean getTapeInView()
    {
        return chickenVision.getEntry("tapeDetected").getBoolean(false);
    }
    public boolean getCargoInView()
    {
        return chickenVision.getEntry("cargoDetected").getBoolean(false);
    }
    public double getTapeYaw()
    {
        return chickenVision.getEntry("tapeYaw").getDouble(0.0);
    }
    public double getCargoYaw()
    {
        return chickenVision.getEntry("cargoYaw").getDouble(0.0);
    }
    public boolean getDriveCamera()
    {
        return chickenVision.getEntry("Driver").getBoolean(false);
    }
    public boolean getTapeCamera()
    {
        return chickenVision.getEntry("Tape").getBoolean(false);
    }
    public boolean getCargoCamera()
    {
        return chickenVision.getEntry("Cargo").getBoolean(false);
    }
    public double getLastTimestamp()
    {
        return chickenVision.getEntry("VideoTimestamp").getDouble(0.0);
    }
}
