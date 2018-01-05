package org.wfrobotics.prototype.subsystems;

import org.wfrobotics.prototype.commands.ArmPivotBase;

import com.ctre.CANTalon;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.command.Subsystem;

/**
 *
 */
public class ArmSubsystem extends Subsystem {

    public DigitalInput CWSwitch;
    public DigitalInput CCWSwitch;
    public DigitalInput UpSwitch;
    public DigitalInput DownSwitch;
    public CANTalon baseMotor;
    public CANTalon elbowMotor1; //possibly change addresses
    public CANTalon elbowMotor2;

    boolean elbow;
    boolean rotate;
    double up; //up is positive
    double clockwise; //clockwise is positive

    // Put methods for controlling this subsystem
    // here. Call these from Commands.
    public ArmSubsystem()
    {

        this.baseMotor = new CANTalon(14);
        this.elbowMotor1 = new CANTalon(15); //possibly change addresses
        this.elbowMotor2 = new CANTalon(16);
        this.CWSwitch = new DigitalInput(0);
        this.CCWSwitch = new DigitalInput(1);
        this.DownSwitch = new DigitalInput(2);
        this.UpSwitch = new DigitalInput(3);


        baseMotor.enableBrakeMode(true);
        elbowMotor1.enableBrakeMode(true);
        elbowMotor2.enableBrakeMode(true);
        elbowMotor2.changeControlMode(CANTalon.TalonControlMode.Follower);
        elbowMotor2.set(15);

       elbowMotor1.setInverted(true);
    }

    public Boolean isAtTop()
    {
        return UpSwitch.get();
    }

    public Boolean isAtBottom()
    {
        return DownSwitch.get();

    }
    public Boolean isAtBaseCW()
    {
        return CWSwitch.get();

    }
    public Boolean isAtBaseCCW()
    {
        return CCWSwitch.get();
    }


        public void initDefaultCommand()
        {

            setDefaultCommand(new ArmPivotBase());
        // Set the default command for a subsystem here.
        //setDefaultCommand(new MySpecialCommand());
    }

}

