package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.SuperStructureBase;

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

    }
}
