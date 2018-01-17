package org.wfrobotics.reuse.commands.vision;

import org.wfrobotics.reuse.subsystems.vision.CameraServer;
import org.wfrobotics.reuse.subsystems.vision.messages.VisionMessageConfig;
import org.wfrobotics.robot.config.VisionMode;

import edu.wpi.first.wpilibj.command.InstantCommand;

/** Extend to create vision mode specific set commands */
public abstract class VisionModeSet extends InstantCommand
{
    public abstract VisionMode getMode();

    protected void initialize()
    {
        VisionMode mode = getMode();
        CameraServer.getInstance().SetConfig(new VisionMessageConfig(mode.getTarget(), mode.getStreamer()));
    }
}
