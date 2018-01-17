package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.Shifting;

import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Subsystem;

public class ShiftingSubsystem extends Subsystem {

    public DoubleSolenoid ShiftingSolenoid;
    boolean isHigh;

    public ShiftingSubsystem()
    {
        this.ShiftingSolenoid = new DoubleSolenoid(7, 0, 1);

        // TODO Auto-generated constructor stub
    }


    public void shiftingSet (boolean high)
    {
        ShiftingSolenoid.set(high ? Value.kForward : Value.kReverse);
        this.isHigh = high;

    }
    public void shiftingReset ()
    {
        ShiftingSolenoid.set(Value.kOff);
    }
    public boolean getShifting()
    {
        return isHigh;
    }

    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new Shifting(false));
        // TODO Auto-generated method stub

    }

}
