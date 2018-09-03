package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.SuperStructureBase;
import org.wfrobotics.robot.commands.ConserveCompressor;

public class SuperStructure extends SuperStructureBase
{
    private static SuperStructure instance = null;

    public static SuperStructure getInstance()
    {
        if (instance == null)
        {
            instance = new SuperStructure();
        }
        return instance;
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new ConserveCompressor());
    }
}
