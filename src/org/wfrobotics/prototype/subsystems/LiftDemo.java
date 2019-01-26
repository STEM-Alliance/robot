package org.wfrobotics.prototype.subsystems;

import java.util.ArrayList;

import org.wfrobotics.prototype.commands.LiftOpenLoop;
import org.wfrobotics.reuse.hardware.TalonFactory;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.command.Subsystem;

public class LiftDemo extends Subsystem
{
    final TalonSRX master;
    final ArrayList<TalonSRX> followers = new ArrayList<TalonSRX>();

    public LiftDemo()
    {
        master = new TalonSRX(10);
        followers.add(TalonFactory.makeFollowerTalon(12, master));
        followers.add(TalonFactory.makeFollowerTalon(14, master));
        followers.add(TalonFactory.makeFollowerTalon(16, master));
    }

    protected void initDefaultCommand()
    {
        setDefaultCommand(new LiftOpenLoop());
    }

    public void setOpenLoop(double percentForward)
    {
        master.set(ControlMode.PercentOutput, percentForward);
    }
}