package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.SuperStructureBase;
import org.wfrobotics.robot.commands.ConserveCompressor;

public class SuperStructure extends SuperStructureBase
{
    private static class SingletonHolder
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
}
