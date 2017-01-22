package org.wfrobotics.subsystems;

import org.wfrobotics.commands.Feed;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;

public class Feeder extends Subsystem {

    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new Feed(Feed.MODE.OFF));
    }

    public void feed()
    {
        DriverStation.reportError("Feeder feed() not implemented yet", true);
    }
}
