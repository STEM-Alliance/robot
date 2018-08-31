package org.wfrobotics.robot.subsystems;

import org.wfrobotics.reuse.subsystems.SuperStructureBase;
import org.wfrobotics.robot.commands.ConserveCompressor;
import org.wfrobotics.robot.config.RobotConfig;

public class SuperStructure extends SuperStructureBase
{
    private static SuperStructure instance = null;

    public SuperStructure(int compressorAddressCAN)
    {
        super(compressorAddressCAN);
    }

    public static SuperStructure getInstance()
    {
        if (instance == null)
        {
            instance = new SuperStructure(RobotConfig.getInstance().CAN_PNEUMATIC_CONTROL_MODULE);
        }
        return instance;
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new ConserveCompressor());
    }
}
