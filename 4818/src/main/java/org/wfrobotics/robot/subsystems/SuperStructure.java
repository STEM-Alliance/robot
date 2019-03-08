package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.EnhancedRobot;
import org.wfrobotics.reuse.hardware.Canifier;
import org.wfrobotics.reuse.hardware.Canifier.RGB;
import org.wfrobotics.reuse.subsystems.SuperStructureBase;
import org.wfrobotics.robot.commands.ConserveCompressor;
import org.wfrobotics.robot.config.RobotConfig;

import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class SuperStructure extends SuperStructureBase
{   
    private final Canifier jeff = new Canifier(6, new RGB(255, 255, 0));
    
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
    }

    public void reportState()
    {
        SmartDashboard.putBoolean("hasHatch", cachedIO.hasHatch);

        SmartDashboard.putBoolean("Cargo In", getHasCargo());
        SmartDashboard.putBoolean("Cargo L", cachedIO.cargoLeft);
        SmartDashboard.putBoolean("Cargo R", cachedIO.cargoRight);
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
    }
    public boolean getHasHatch()
    {
        return cachedIO.hasHatch;
    }
    boolean lastCargo = false;
    double cargoTimeout = 0;
    public boolean getHasCargo()
    {
        if (cachedIO.cargoRight && cachedIO.cargoLeft)
        {
            if(lastCargo == false)
            {
                cargoTimeout = Timer.getFPGATimestamp();
            }
            lastCargo = true;
        } else{ lastCargo = false; }

        boolean output = false;
        if(lastCargo){
            if ((Timer.getFPGATimestamp() - cargoTimeout) >= RobotConfig.getInstance().kIntakeDistanceTimeout)
            {
                output = true;
            }
        }
        return output;
    }
}
