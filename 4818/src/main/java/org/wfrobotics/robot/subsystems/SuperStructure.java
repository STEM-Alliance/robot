package org.wfrobotics.robot.subsystems;

import java.util.ArrayList;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.hardware.Canifier;
import org.wfrobotics.reuse.hardware.Canifier.RGB;
import org.wfrobotics.reuse.hardware.sensors.SharpDistance;
import org.wfrobotics.reuse.subsystems.SuperStructureBase;
import org.wfrobotics.robot.commands.ConserveCompressor;
import org.wfrobotics.robot.config.RobotConfig;

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

        SmartDashboard.putBoolean("Cargo In", getHasCargo());
        SmartDashboard.putBoolean("Cargo L", cachedIO.cargoLeft);
        SmartDashboard.putBoolean("Cargo R", cachedIO.cargoRight);

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

    public boolean getHasHatch()
    {
        return cachedIO.hasHatch;
    }

    double cargoTimeout = 0;
    
    public boolean getHasCargo()
    {
        if (cachedIO.cargoRight || cachedIO.cargoLeft)
        {
            cargoTimeout = Timer.getFPGATimestamp();
            if ((Timer.getFPGATimestamp() - cargoTimeout) >= RobotConfig.getInstance().kIntakeDistanceTimeout)
            {
                return true;
            }
        }
        return false;
    }

    public double getAngleFromWall() {
        return Math.toDegrees(Math.atan((cachedIO.distanceLeft - cachedIO.distanceRight)/15.0));
    }

	public double getDistanceFromWall() {
		return (cachedIO.distanceLeft + cachedIO.distanceRight) / 2.0;
	}
}
