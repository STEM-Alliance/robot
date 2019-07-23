package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.hardware.Canifier;
import org.wfrobotics.reuse.hardware.Canifier.RGB;
import org.wfrobotics.reuse.subsystems.SuperStructureBase;
import org.wfrobotics.reuse.utilities.CircularBuffer;
import org.wfrobotics.robot.commands.CommandTemplate;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public final class SuperStructure extends SuperStructureBase
{
    static class SingletonHolder
    {
        static SuperStructure instance = new SuperStructure();
    }

    public static SuperStructure getInstance()
    {
        return SingletonHolder.instance;
    }

    public SuperStructure()
    {

    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new CommandTemplate());
    }

    public void cacheSensors(boolean isDisabled)
    {
              
    }

    public void reportState()
    {
        
    }

    @Override
    public TestReport runFunctionalTest()
    {
        TestReport report = super.runFunctionalTest();
        
        report.addManualTest("Tested Jeff's LED's");

        return report;
    }
}
