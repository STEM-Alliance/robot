package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.ArmPivotHand;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.command.Subsystem;

public class HandSubsystem extends Subsystem {

    DoubleSolenoid handSolenoid = new DoubleSolenoid(5, 6); //if we want this to work we need to change go (9, 0 , 1)
    public DigitalInput CCWSensor;
    public DigitalInput CWSensor;

    public CANTalon handMotor;


    public HandSubsystem()
    {
        this.CCWSensor = new DigitalInput(5);
        this.CWSensor = new DigitalInput(4);
        this.handMotor =  new CANTalon(17);
        handMotor.enableBrakeMode(true);
        // TODO Auto-generated constructor stub
    }

    public HandSubsystem(String name)
    {
        super(name);
        // TODO Auto-generated constructor stub
    }

    public void moveHand (boolean in)
    {
        handSolenoid.set(in ? Value.kForward : Value.kReverse);
    }
    public void handReset ()
    {
        handSolenoid.set(Value.kOff);
    }
    @Override
    protected void initDefaultCommand()
    {
        setDefaultCommand(new ArmPivotHand(0));
        // TODO Auto-generated method stub

    }
    public Boolean isAtHandCW()
    {
        return CWSensor.get();

    }
    public Boolean isAtHandCCW()
    {
        return CCWSensor.get();
    }

}
